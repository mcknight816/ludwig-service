package com.bluntsoftware.ludwig.conduit.activities.output;


import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyType;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import com.bluntsoftware.ludwig.controller.AssetController;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class HttpClientActivity extends ActivityImpl {

    @Autowired
    AssetController assetService;

    private @Autowired
    HttpServletRequest httpServletRequest;

    public HttpClientActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title(this.getName()).build();
        List<String> type = new ArrayList<String>();
        type.add("post");
        type.add("get");
        type.add("delete");
        type.add("put");
        type.add("upload");

        schema.addEnum("type",type ,"json");
        schema.addString("url");
        schema.addString("username");
        schema.addString("password",StringProperty.builder().defaultValue("").format(PropertyFormat.PASSWORD).build());
        schema.addString("payload",StringProperty.builder().defaultValue("{}").type(PropertyType.HIDDEN.getValue()).build());
        schema.addString("file",StringProperty.builder().defaultValue("http://someserver/someFile.jpg").format(PropertyFormat.IMAGE_CHOOSER).build());
        schema.addString("content-type");
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)   {
        HttpRequestBase request = null;
        Map<String, Object> ret = new HashMap<>();
        ret.put("data","");
        try {
            request = getRequest(input);

        if(request != null){

            HttpClient client = HttpClientBuilder.create().build();

            Object userNameObj = input.get("username") ;
            Object passwordObj = input.get("password") ;
            if(userNameObj != null && passwordObj != null){
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userNameObj.toString(),passwordObj.toString());
                credentialsProvider.setCredentials(AuthScope.ANY,creds );
                client = HttpClientBuilder.create()
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .build();
                request.addHeader(new BasicScheme().authenticate(creds, request,null));
            }
            String type = input.get("content-type").toString();
            if(type != null && !type.equalsIgnoreCase("")){
                request.setHeader("Accept",type);
            }

            HttpResponse response =  client.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            Header contentType = response.getFirstHeader("content-type");

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            if(contentType.getValue().contains("json")){
                ret.put("data",convertStringToMap(result.toString()));
            }else{
                ret.put("data",result.toString());
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
            ret.put("error",e.getMessage());
        }
        return ret;
    }

    Map<String,Object> convertStringToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if(json.charAt(0) == '['){//its an array
            Map<String,Object> ret = new HashMap<>();
            TypeReference<List<Map<String,Object>>> typeRef = new TypeReference<List<Map<String,Object>>>() {};
            ret.put("rows",mapper.readValue(json, typeRef));
            return ret;
        }else{
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
            return mapper.readValue(json, typeRef);
        }
    }
    String convertMapToString(Object map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
    private HttpRequestBase getRequest(Map<String, Object> input) throws IOException {
         String type = input.get("type").toString();
         if(type.equalsIgnoreCase("get")){
             return httpGet(input);
         }else if(type.equalsIgnoreCase("post")){
             return httpPost(input);
         }else if(type.equalsIgnoreCase("put")){
             return httpPut(input);
         }else if(type.equalsIgnoreCase("delete")){
             return httpDelete(input);
         }else if(type.equalsIgnoreCase("upload")){
             return httpUpload(input);
         }
         return null;
    }
    private HttpGet httpGet(Map<String, Object> input) throws UnsupportedEncodingException {
        HttpGet request = new HttpGet();
        request.setURI(getURI(input));
        return request;
    }
    private HttpPost httpUpload(Map<String, Object> input) throws IOException {
        HttpPost request = new HttpPost();
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
       // entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        Object fileObj = input.get("file");
        if(fileObj != null) {
            File file = assetService.getFolder(fileObj.toString());
            entityBuilder.addBinaryBody("file", file,
                    ContentType.create("application/octet-stream"), file.getName());
        }
        Object payload = input.get("payload");
        if(payload != null){
            entityBuilder.addTextBody("payload", convertMapToString(payload));

        }
        request.setEntity(entityBuilder.build());
        request.setURI(URI.create(getURL(input)));
        return request;
    }
    private HttpPost httpPost(Map<String, Object> input) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost();
        Object payload = input.get("payload");
        if(payload != null){
            StringEntity params =new StringEntity(convertMapToString(payload));
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
        }

        request.setURI(URI.create(getURL(input)));
        request.setHeader("content-type","application/json");
        return request;
    }
    private HttpDelete httpDelete(Map<String, Object> input){
        HttpDelete request = new HttpDelete();
        request.setURI(URI.create(getURL(input)));
        return request;
    }
    private HttpPut httpPut(Map<String, Object> input){
        HttpPut request = new HttpPut();
        request.setURI(URI.create(getURL(input)));
        return request;
    }

    private String getURL(Map<String, Object> in){
        String ret =  in.get("url").toString();
        if (!ret.startsWith("http")) {
            StringBuffer url = httpServletRequest.getRequestURL();
            String uri = httpServletRequest.getRequestURI();
            String ctx = httpServletRequest.getContextPath();
            String base = url.substring(0, url.length() - uri.length() + ctx.length());
            ret = base + ret;
        }
        return ret;
    }

    private  URI getURI(Map<String, Object> in) throws UnsupportedEncodingException {
        URI uri = URI.create(getURL(in));
        String queryString =  uri.getQuery();

        if(queryString == null){
            queryString = "";
        }

        List<NameValuePair> params = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        Object obj =  in.get("context");
        if(obj != null && obj instanceof Map){
            Map payload = (Map)obj;
            for(Object key:payload.keySet()) {
                NameValuePair nvp = new BasicNameValuePair(key.toString(), payload.get(key).toString());
                params.add(nvp);
            }
        }
        String newQueryStringEncoded = URLEncodedUtils.format(params, StandardCharsets.UTF_8);
        if(!newQueryStringEncoded.equalsIgnoreCase("")){
            newQueryStringEncoded = "?"+ newQueryStringEncoded;
        }
        //final String newQueryStringDecoded = URLDecoder.decode(newQueryStringEncoded, StandardCharsets.UTF_8.toString());
        String port = "";
        if(uri.getPort() != -1){
            port = ":" + uri.getPort();
        }
        String url = uri.getScheme() + "://" + uri.getHost() + port + uri.getPath() + newQueryStringEncoded;
        return URI.create(url);

    }

    public static void main(String[] args) {
        HttpClientActivity httpClientActivity = new HttpClientActivity(null);
        Map<String, Object> payload = new HashMap<>();
        payload.put("name","fred");
        Map<String, Object> in = new HashMap<>();
        in.put("type","get");
        in.put("url","https://api.koleimports.com/products?limit=25&offset=0");//mcknight816@gmail.com:84f54dad04ba2774aa9b85b96d805954edec5f51@
        //in.put("payload",payload);
        in.put("content-type","application/vnd.koleimports.ds.product+json");
        in.put("username","X59524");
        in.put("password","84f54dad04ba2774aa9b85b96d805954edec5f51");
        try {
            Map<String, Object> out = httpClientActivity.run(in);
            System.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
