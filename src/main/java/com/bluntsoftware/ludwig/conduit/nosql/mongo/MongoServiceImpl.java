package com.bluntsoftware.ludwig.conduit.nosql.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 10/12/2016.
 *
 */

public class MongoServiceImpl {
     private final MongoRepository mongoRepository;


    public MongoServiceImpl(MongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }


    @RequestMapping(
            value = "{databaseName}/dump",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<InputStreamResource> mongoDump( @PathVariable("databaseName") String databaseName,
                                                HttpServletRequest request){
        InputStreamResource is =  null;
        HttpHeaders respHeaders = new HttpHeaders();
        try {
            MongoCLI mongoCLI = new MongoCLI();
            File backup = mongoCLI.backupDatabase(databaseName);
            is =  new InputStreamResource(new FileInputStream(backup));
            respHeaders.setContentType(MediaType.valueOf("application/json"));
            respHeaders.setContentLength(backup.length());
            respHeaders.setContentDispositionFormData("attachment", databaseName+ "-database.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<InputStreamResource>(is, respHeaders, HttpStatus.OK);
    }
    @RequestMapping(
            value = "restoredump",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    void  restoreDump(@RequestParam("file") MultipartFile file){

    }

    @RequestMapping(
            value = "{databaseName}/backup",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<InputStreamResource> backup( @PathVariable("databaseName") String databaseName,
                                                HttpServletRequest request){
        InputStreamResource isr = null;
        HttpHeaders respHeaders = new HttpHeaders();
        try {
            Map<String,Object> data = mongoRepository.backupDatabase(databaseName);
            ObjectMapper mapper = new ObjectMapper();
            byte[] dataBuffer =  mapper.writeValueAsBytes(data);
            isr = new InputStreamResource(new ByteArrayInputStream(dataBuffer));
            respHeaders.setContentType(MediaType.valueOf("application/json"));
            respHeaders.setContentLength(dataBuffer.length);
            respHeaders.setContentDispositionFormData("attachment", databaseName+ "-database.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    }
    @RequestMapping(
            value = "restore",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    void restore(@RequestParam("file") MultipartFile file){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data =  mapper.readValue(file.getInputStream(),Map.class);
            mongoRepository.restoreDatabase(data);
        }catch (IOException e){
             e.printStackTrace();
        }
    }
    @RequestMapping(
            value = "{databaseName}/import",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    void importCollections(@PathVariable("databaseName") String databaseName,@RequestParam("file") MultipartFile file){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data =  mapper.readValue(file.getInputStream(),Map.class);
            mongoRepository.restoreCollections(databaseName,data);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @RequestMapping(
            value = "{databaseName}/{collectionName}/export",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<InputStreamResource> export( @PathVariable("databaseName") String databaseName,
                                                @PathVariable("collectionName") String collectionName,
                                                HttpServletRequest request){
        InputStreamResource isr = null;
        HttpHeaders respHeaders = new HttpHeaders();
        try {
            Map<String,Object> data = new HashMap<>();
            data.put("name",databaseName);
            Map<String,Object> collections = new HashMap<>();
            List<Document>  collection = mongoRepository.backupCollection(databaseName,collectionName);
            collections.put(collectionName,collection);
            data.put("collections",collections);
            ObjectMapper mapper = new ObjectMapper();
            byte[] dataBuffer =  mapper.writeValueAsBytes(data);
            isr = new InputStreamResource(new ByteArrayInputStream(dataBuffer));
            respHeaders.setContentType(MediaType.valueOf("application/json"));
            respHeaders.setContentLength(dataBuffer.length);
            respHeaders.setContentDispositionFormData("attachment", collectionName+ "-collection.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    }



    @RequestMapping(
            value = "{databaseName}/{collectionName}/doc/save",
            method = { RequestMethod.GET,RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public  Object saveUpdateParams(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            HttpServletRequest request) throws Exception{

        Map<String,Object> object = new HashMap<>();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String parameterName = (String) enumeration.nextElement();
            object.put(parameterName,request.getParameter(parameterName));
        }
        return mongoRepository.save(databaseName,collectionName,object,true);
    }
    @RequestMapping(
            value = "{databaseName}",
            method = {RequestMethod.DELETE},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object deleteDatabase(
            @PathVariable("databaseName") String databaseName) throws Exception{
        return mongoRepository.deleteDatabase(databaseName);
    }
    @RequestMapping(
            value = "{databaseName}/{collectionName}",
            method = {RequestMethod.DELETE},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object deleteCollection(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName) throws Exception{
          return mongoRepository.deleteCollection(databaseName,collectionName);
    }

    @RequestMapping(
            value = "{databaseName}/{collectionName}",
            method = {RequestMethod.POST,RequestMethod.PUT},
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public  Object saveUpdate(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            @RequestBody Map<String,Object> object) throws Exception{
        return mongoRepository.save(databaseName,collectionName,object,true);
    }

    //Save
    @RequestMapping(
            value="{databaseName}/{collectionName}/{id}",
            method = { RequestMethod.POST,RequestMethod.PUT},
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    protected   Object save(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            @PathVariable("id") String id,
            @RequestBody Map<String,Object> object) throws Exception{

        Document myDoc = mongoRepository.getById(id,databaseName,collectionName);
        if(myDoc != null){
            myDoc.putAll(object);
        }
        return mongoRepository.save(databaseName,collectionName,myDoc,true);
    }
    public static String validString(String value, String defaultValue){
        if (isValidParameter(value)) {
            return value;
        }
        return defaultValue;
    }
    private static boolean isValidParameter(String param) {
        return param != null && !param.isEmpty() && !param.equalsIgnoreCase("_empty") && !param.equalsIgnoreCase("undefined") && !param.equalsIgnoreCase("null");
    }

    //List
    @RequestMapping(
            value = {"{databaseName}/{collectionName}","{databaseName}/{collectionName}/data"},
            method = {RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public  Object findAll(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            HttpServletRequest request) throws Exception{
        String filterByFields =  validString(request.getParameter("filterByFields"), "{}");
        String projection =  validString(request.getParameter("projection"), "{}");
        String rows = validString(request.getParameter("rows"),"25");
        String page = validString(request.getParameter("page"),"1");
        String sord = validString(request.getParameter("sord"),null);
        String sidx = validString(request.getParameter("sidx"),null);
        return mongoRepository.findAll(databaseName,collectionName,filterByFields,projection,sidx,sord,rows,page);
    }

    //Get
    @RequestMapping(
            value = "{databaseName}/{collectionName}/{id}",
            method = {RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object findOne(@PathVariable("databaseName") String databaseName,
                          @PathVariable("collectionName") String collectionName,
                          @PathVariable("id") String id) throws Exception{
        return mongoRepository.getById(id,databaseName,collectionName);
    }

    //Remove
    @RequestMapping(
            value = "{databaseName}/{collectionName}/{id}",
            method = {RequestMethod.DELETE},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object delete(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            @PathVariable("id") String id) throws Exception{
        return mongoRepository.remove(databaseName,collectionName,id);
    }

    //Show Schema
    @RequestMapping(
            value = "{databaseName}/{collectionName}/schema",
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public  Map<String, Object> showSchema(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName){

        MongoCollection<Document> collection = mongoRepository.getCreateCollection(databaseName,collectionName);
        return collection.find().first();
    }

    //List Columns
    @RequestMapping(
            value = "{databaseName}/{collectionName}/columns",
            method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<String> columns(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName){
        return mongoRepository.columns(databaseName,collectionName);
    }



    @RequestMapping( value = "")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Map> listDatabases(){
        return mongoRepository.listDatabases();
    }

    //get api
    @RequestMapping( value = {"{databaseName}/{collectionName}/api","{database}/api"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getApi(
            @PathVariable("databaseName") String databaseName,
            @PathVariable("collectionName") String collectionName,
            HttpServletRequest request){
      //  MongoCollection<Document> collection = mongoRepository.getCreateCollection(databaseName,collectionName);
        return config(databaseName,collectionName,request);
    }
    @RequestMapping( value = "api")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> api(
            HttpServletRequest request){
        Map<String,Object> ret =  getServerInfo(request);

        MongoClient client = mongoRepository.getClient();
        Map<String,Map> databaseMap = new HashMap<>();
        for(Document db:client.listDatabases()){
            String databaseName = (String)db.get("name");
            MongoDatabase database =  client.getDatabase(databaseName);

            Map<String,Map> collectionMap = new HashMap<>();
            for(Document collection:database.listCollections()){
                String collectionName = (String)collection.get("name");

                collectionMap.put(collectionName,config(databaseName,collectionName,request));
            }
            databaseMap.put(databaseName,collectionMap);

        }
        ret.put("mods",databaseMap);
        ret.put("serverConfig",getServerInfo(request));
        return ret;
    }

    @RequestMapping( value = "{databaseName}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Object listCollections(
            @PathVariable("databaseName") String databaseName){
        MongoClient client = mongoRepository.getClient();
        MongoDatabase database =  client.getDatabase(databaseName);
        return database.listCollections();
    }
    private Map<String,Object> getServerInfo(HttpServletRequest request){

        String serverPath = "http://" + request.getServerName();
        Integer port = request.getLocalPort();
        String contextPath = request.getContextPath();
        if(!port.toString().equalsIgnoreCase("")){
            serverPath += ":" + port;
        }
        if(contextPath != null ){
            serverPath += "/" + contextPath;
        }

        serverPath += "/mongo/";

        Map<String,Object>  map = new HashMap<>();
        map.put("localAddress",request.getLocalAddr()); //- the server's IP address as a string
        map.put("localName",request.getLocalName()); //- the name of the server recieving the request
        map.put("serverName",request.getServerName()); //- the name of the server that the request was sent to
        map.put("port",request.getLocalPort()); //- the port the server recieved the request on
        map.put("serverPort",request.getServerPort()); //- the port the request was sent to
        map.put("contextPath",request.getContextPath()); //- the part of the path that identifies the application
        map.put("serverPath",serverPath);
        return map;
    }
    public Map<String,Object> config(String qualifier,String name,HttpServletRequest request) {
        Map<String,Object> serverInfo = getServerInfo(request);
        String address =  serverInfo.get("serverPath") + qualifier + "/" +  name + "/" ;
        Map<String,Object>  map = new HashMap<>();
        map.put("mod",qualifier);
        map.put("name",name);
        map.put("schema",address + "schema");
        map.put("columns",address +"columns");
        map.put("api",address +  "api");
        map.put("data",address + "data");
        return map;
    }
}
