package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.model.Application;
import com.bluntsoftware.ludwig.model.Flow;
import com.bluntsoftware.ludwig.model.FlowActivity;
import com.bluntsoftware.ludwig.model.FlowConfig;
import com.bluntsoftware.ludwig.repository.ApplicationRepository;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class OpenApiService {

    private final ApplicationRepository applicationRepository;
    private final PayloadSchemaConfig payloadSchemaConfig;
    private final FlowConfigRepository configRepository;
    public OpenApiService(ApplicationRepository applicationRepository, PayloadSchemaConfig payloadSchemaConfig, FlowConfigRepository configRepository) {
        this.applicationRepository = applicationRepository;
        this.payloadSchemaConfig = payloadSchemaConfig;
        this.configRepository = configRepository;
    }
    private static final Map<String, Map<String, Object>> activityDetails;
    static {
        Map<String, Map<String, Object>> aMap = new HashMap<>();
        aMap.put("Get",getDefaultDetails("get","List Items"));
        aMap.put("GetById",getDefaultDetails("get","Get an Item by Id"));
        aMap.put("Post",getDefaultDetails("post","Create or Edit an Item"));
        aMap.put("Delete",getDefaultDetails("delete","Remove an Item by Id"));
        aMap.put("Columns",getDefaultDetails("get","List input fields"));
        aMap.put("Upload",getDefaultDetails("post","Upload Files"));
        activityDetails = Collections.unmodifiableMap(aMap);
    }
    private static Map<String, Object> getDefaultDetails(String type, String description){
        Map<String, Object> details = new HashMap<>();
        details.put("type",type);
        details.put("description",description);
        return details;
    }
    public Map<String,Object> paths(Application app){

        List<Flow> flows = Objects.requireNonNull(app).getFlows();
        Map<String,Object> ret = new HashMap<>();
        for(Flow flow:flows){
            for(FlowActivity flowActivity:flow.getActivities()) {
                try {
                    String category = flowActivity.getCategory();
                    if (category != null && category.equalsIgnoreCase("input")) {
                        String path = getPath(flow.getPath() != null && flow.getPath().equalsIgnoreCase("") ? flow.getPath() :flow.getName().toLowerCase().replace(" " , "-"), flowActivity,app);
                        Map entity = (Map) ret.get(path);
                        if (entity == null) {
                            entity = method(flow,flowActivity);
                        } else {
                            entity.putAll(method(flow,flowActivity));
                        }
                        ret.put(path, entity);
                    }
                }catch(Exception e){

                }
            }
        }
        return ret;
    }
    private String getPath(String flowName, FlowActivity flowActivity,Application app){
        String appPath = app.getPath() != null && !app.getPath().equalsIgnoreCase("") ?
                app.getPath() : app.getName().toLowerCase().replace(' ','-');
        String path = "/api/" + appPath + "/" + flowName;
        String name =   flowActivity.getName(); //.getActivity()
        String context = flowActivity.getContext();
        if(context != null && !context.equalsIgnoreCase("")){
            path += "/action/" + context;
        }
        if(name.equalsIgnoreCase("GetById") || name.equalsIgnoreCase("Delete")){
            path += "/{id}";
        }
        if(name.equalsIgnoreCase("Columns")){
            path += "/columns";
        }
        if(name.equalsIgnoreCase("Upload")){
            path += "/upload";
        }
        return path;
    }

    private Map method(Flow flow,FlowActivity flowActivity){
        String activityName =   flowActivity.getName();//.getActivity()
        Map<String,Object> details = activityDetails.get(activityName);
        String description = flowActivity.getDescription();
        Map<String,Object> props = new HashMap<>();
        props.put("description",details.get("description"));
        if(description != null && !description.equalsIgnoreCase("")){
            props.put("description", description);
        }
        List<String> tags = new ArrayList<>();
        tags.add(flow.getName());
        props.put("tags",tags);

        String type = details.get("type").toString();

        props.putAll(input( flowActivity));
        props.putAll(output( flowActivity));

        Map<String,Object> ret = new HashMap<>();
        ret.put(type,props);

        return ret;
    }
    Map<String,Object> input(FlowActivity flowActivity){
        Map<String,Object> ret = new HashMap<>();
        Map<String,Object> input = flowActivity.getInput();
        List<Map<String,Object>>  params = new ArrayList<>();
        switch(flowActivity.getName()){//.getActivity()
            case "Get":
                params.add(getQueryInputParameters("rows",false,"string","20","max limit"));
                params.add(getQueryInputParameters("page",false,"string","1","offset (rows x page)"));
                params.add(getQueryInputParameters("sord",false,"string","ASC","ASC , DESC"));
                params.add(getQueryInputParameters("sidx",false,"string","_id","sort index i.e. id"));
                params.add(getQueryInputParameters("filterByFields",false,"object",null,"{\"id\":\"xxxx-xxx-xx\"}"));
                ret.put("parameters", params);
                break;
            case "GetById":
                params.add(getPathInputParameters("id",true,"string","id of the item to get"));
                ret.put("parameters", params);
                break;
            case "Post":
                if(input.containsKey("payload")){
                    Object schemaName = input.get(payloadSchemaConfig.getPropertyName());
                    ret.put("requestBody" ,getJsonPostRequestBody(input.get("payload"),  getPayloadSchema(schemaName)));
                }

                break;
            case "Delete":
                params.add(getPathInputParameters("id",true,"string","id of the item to delete"));
                ret.put("parameters", params);
                break;
            case "Columns":
                break;
            case "Upload":
                List<String> consumedDataTypes  = new ArrayList<>();
                consumedDataTypes.add("multipart/form-data");
                ret.put("consumes",consumedDataTypes);

                Map<String,Object> formData = new HashMap<>();
                formData.put("in","formData");
                formData.put("type","file");
                formData.put("name","file");
                formData.put("descriptions","file to upload.");
                params.add(formData);
                ret.put("parameters", params);
                if(input.containsKey("payload")){
                    Object schemaName = input.get(payloadSchemaConfig.getPropertyName());
                    ret.put("requestBody" ,getJsonPostRequestBody(input.get("payload"),  getPayloadSchema(schemaName)));
                }
                /* if(input.containsKey("payload")){
                    Map<String,Object> file = new HashMap<>();
                    file.put("type","file");
                    file.put("format","binary");
                    Map<String,Object> properties = new HashMap<>();
                    properties.put("file",file);

                    Object schemaName = input.get(payloadSchemaConfig.getPropertyName());
                    Map<String,Object> schema = getPayloadSchema(schemaName);
                    schema.put("properties",properties);
                    ret.put("requestBody" ,getJsonUploadRequestBody(input.get("payload"),schema));
                 } */
                break;

        }

        return ret;
    }
    Map<String,Object> output( FlowActivity flowActivity){
        Map<String,Object> ret = new HashMap<>();
        Map<String,Object> output = flowActivity.getInput();
        if(output.containsKey("payload")){
            ret.put("responses",get200Response(null));
        }
        return ret;
    }

    private Map<String,Object> get200Response(Object output) {
        Map<String,Object> response200 = new HashMap<>();
        response200.put("description","OK");
        //response200.putAll(getJsonPostRequestBody(output));
        Map<String,Object> responses = new HashMap<>();
        responses.put("200",response200);
        return responses;
    }
    Map<String,Object> getPayloadSchema(Object name){
        Map<String,Object> schema = null;

        Mono<FlowConfig> flowConfig = configRepository.findByNameAndConfigClass(name.toString(), PayloadSchemaConfig.class.getName());
        Map<String,Object> payload = Optional.of(Objects.requireNonNull(flowConfig.block()).getConfig()).orElse(null);
        if(payload != null && payload.containsKey("PayloadSchema")){
            Map<String,Object> payloadSchema = (Map<String,Object>)payload.get("PayloadSchema");
            if(payloadSchema.containsKey("schema")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    schema = mapper.readValue(payloadSchema.get("schema").toString(),HashMap.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        if(schema == null){
            schema = new HashMap<>();
            schema.put("type","object");
            schema.put("additionalProperties",true);
        }
        return schema;
    }
    Map<String,Object> getJsonPostRequestBody(Object example,Map<String,Object> schema){
        Map<String,Object> appRoot = new HashMap<>();
        appRoot.put("schema",schema);
        if(example != null){
            appRoot.put("example",example);
        }
        Map<String,Object> applicationType = new HashMap<>();
        applicationType.put("application/json",appRoot);
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("content",applicationType);
        return requestBody;
    }


    Map<String,Object> getHeaderInputParameters(String name,boolean required,String type,String description){
        Map<String,Object> schema = new HashMap<>();
        schema.put("type",type);
        return getInputParameters("header",name,required,schema,description);
    }

    Map<String,Object> getQueryInputParameters(String name,boolean required,String type,Object defaultValue,String description){
        Map<String,Object> schema = new HashMap<>();
        schema.put("type",type);
        if(defaultValue != null){
            schema.put("default",defaultValue);
        }
        return getInputParameters("query",name,required,schema,description);
    }

    Map<String,Object> getPathInputParameters(String name,boolean required,String type,String description){
        Map<String,Object> schema = new HashMap<>();
        schema.put("type",type);
        return getInputParameters("path",name,required,schema,description);
    }

    Map<String,Object> getCookieInputParameters(String name,boolean required,String type,String description){
        Map<String,Object> schema = new HashMap<>();
        schema.put("type",type);
        return getInputParameters("cookie",name,required,schema,description);
    }

    Map<String,Object> getInputParameters(String in, String name,boolean required, Map<String,Object> schema,String description ){
        Map<String,Object> ret = new HashMap<>();
        ret.put("in",in);
        ret.put("name",name);
        ret.put("schema",schema);
        ret.put("required",required);
        ret.put("description",description);
        return ret;
    }

    public Map<String, Object> openApi(String id) {

        Application application = applicationRepository.findById(id).block();

        Map<String,Object> info = new HashMap<>();
        Map<String,Object> openApi = new HashMap<>();
        info.put("description",application.getDescription());
        info.put("version","1.0.3");
        info.put("title",application.getName());
        openApi.put("openapi","3.0.0");

        openApi.put("info",info);
        openApi.put("url","http://localhost:9094");
        openApi.put("paths",paths(application));
        return openApi;
    }
}