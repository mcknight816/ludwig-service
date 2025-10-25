package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.config.model.PayloadSchemaConfig;
import com.bluntsoftware.ludwig.conduit.config.model.domain.PayloadSchema;
import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.domain.FlowConfig;
import com.bluntsoftware.ludwig.domain.McpServerTool;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.dto.ToolSelection;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;

import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonSchemaToModel;
import com.bluntsoftware.ludwig.utils.converter.impl.ModelToJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

@Slf4j
@Service
public class McpService {

    private final ApplicationService applicationService;
    private final FlowConfigRepository configRepository;
    private final PayloadSchemaConfig payloadSchemaConfig;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService;
    private final FlowRunnerService flowRunnerService;
    public McpService(ApplicationService applicationService, FlowConfigRepository configRepository, PayloadSchemaConfig payloadSchemaConfig, ObjectMapper objectMapper, OpenAiService openAiService, FlowRunnerService flowRunnerService) {
        this.applicationService = applicationService;
        this.configRepository = configRepository;
        this.payloadSchemaConfig = payloadSchemaConfig;
        this.objectMapper = objectMapper;
        this.openAiService = openAiService;
        this.flowRunnerService = flowRunnerService;
    }

    public Mono<List<McpServerTool>> listTools(String[] apps) {
        return applicationService.findAll()
                .filter(app -> apps != null && (apps.length == 0 || Arrays.asList(apps).contains(app.getId())) )
                .flatMap(app -> Flux.fromIterable(app.getFlows())
                        .flatMap(flow -> Flux.fromIterable(flow.getActivities())
                                .filter(fa -> fa.getCategory().equalsIgnoreCase("input"))
                                .map(fa -> McpServerTool.builder()
                                        .name(app.getName() + "-" + flow.getName() + "-" + fa.getName() +
                                                (fa.getContext() != null && !fa.getContext().isEmpty() ? "-" + fa.getContext() : ""))
                                        .description(fa.getDescription())
                                        .inputSchema(getInputSchema(fa))
                                        .flow(flow)
                                        .flowActivity(fa)
                                        .build()
                                )
                        )
                )
                .collectList();
    }

    Optional<McpServerTool> findTool(String name,String[] apps){
        return Objects.requireNonNull(listTools(apps).block()).stream()
                .filter(t -> t.getName().equalsIgnoreCase(name)).findFirst();

    }

    public ToolSelection selectToolWithAi(String userPrompt,String[] apps ) {
        return selectToolWithAi(userPrompt,  listTools(apps).block());
    }

    public String run(ToolSelection toolSelection,String[] apps){
        McpServerTool mcpServerTool = findTool(toolSelection.getTool(),apps).orElse(null);
        String ret = " Tool Not Found !";
        if(mcpServerTool != null){
           log.info("Mcp Server Tool: {}",mcpServerTool);
           log.info("Tool Selection: {}",toolSelection);
           ret = " Found mcp server tool " + mcpServerTool.getName() + " running with " + toolSelection.getArgs();
        }
        return "Results are : " + ret;
    }

    private ToolSelection selectToolWithAi(String userPrompt, List<McpServerTool> tools) {
        try {
            String toolsSummary = tools.stream()
                    .map(t -> Map.of(
                            "name", t.getName(),
                            "description", Optional.ofNullable(t.getDescription()).orElse(""),
                            "inputSchema", Optional.ofNullable(t.getInputSchema()).orElse(Map.of())
                    ))
                    .toList()
                    .toString();

            String selectorPrompt =
                    "You are an assistant that selects the best tool and arguments for a user's request.\n" +
                            "Return ONLY a compact JSON object with fields: tool (string) and args (object).\n" +
                            "If no tool is appropriate, return: {\"tool\":\"none\",\"args\":{}}.\n\n" +
                            "Available tools (name, description, input_schema):\n" + toolsSummary + "\n\n" +
                            "User request:\n" + userPrompt + "\n\n" +
                            "JSON response with no commentary:";

            AIService aiService = openAiService.getOpenAiService(null);
            String ai = aiService.completions(AICompletionRequest.builder()
                    .message(AIMessage.builder().role("user").content(selectorPrompt).build())
                    .model(OpenAiModel.GPT_4_MINI.getValue())
                    .build()).getChoices().get(0).getMessage().getContent();

            return parseSelection(ai);
        } catch (Exception e) {
            log.warn("Failed to select tool with AI: {}", e.getMessage());
            return ToolSelection.builder().tool("none").args(Map.of()).build();
        }
    }

    private ToolSelection parseSelection(String json) {
        try {
            return objectMapper.readValue(json, ToolSelection.class);
        } catch (Exception e) {
            log.debug("Primary parse failed, trying to extract JSON substring: {}", e.getMessage());
            String extracted = extractFirstJsonObject(json);
            if (extracted != null) {
                try {
                    return objectMapper.readValue(extracted, ToolSelection.class);
                } catch (Exception ignore) {
                }
            }
            return ToolSelection.builder().tool("none").args(Map.of()).build();
        }
    }
    // Very simple JSON object extractor as a fallback
    private String extractFirstJsonObject(String s) {
        int start = s.indexOf('{');
        if (start < 0) return null;
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            if (c == '}') depth--;
            if (depth == 0) {
                return s.substring(start, i + 1);
            }
        }
        return null;
    }



    //TODO : refine to be more dynamic and really describe the parameters required and defaults
    Map<String,Object> getInputSchema(FlowActivity flowActivity){
        Map<String,Object> ret = new HashMap<>();
        Map<String,Object> input = flowActivity.getInput();
        List<Map<String,Object>>  params = new ArrayList<>();

        params.add(getHeaderInputParameters("tenant-id",true,"string", TenantResolver.resolve(),"tenant-id"));

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
                if(input.containsKey("payloadSchema")){
                    Object schemaName = input.get("payloadSchema");
                    ret.put("requestBody" ,getJsonPostRequestBody(input.get("payload"),  getPayloadSchema(schemaName)));
                }
                ret.put("parameters", params);
                break;
            case "Delete":
                params.add(getPathInputParameters("id",true,"string","id of the item to delete"));
                ret.put("parameters", params);
                break;
            case "Columns":
                ret.put("parameters", params);
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
    Map<String,Object> getPayloadSchema(Object name){
        Map<String,Object> schema = null;
        if(name != null && !name.toString().equalsIgnoreCase("")) {
            Mono<FlowConfig> flowConfigMono = configRepository.findByNameAndConfigClass(name.toString(), PayloadSchema.class.getName());
            FlowConfig flowConfig = flowConfigMono.block();
            if (flowConfig != null) {
                Map<String, Object> config = Optional.of(flowConfig.getConfig()).orElse(null);
                if (config != null && config.containsKey("schema")) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        schema = mapper.readValue(config.get("schema").toString(), HashMap.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    //}
                }
            }

            if (schema == null) {
                schema = new HashMap<>();
                schema.put("type", "object");
                schema.put("additionalProperties", true);
            }
        }
        return schema;
    }

    Map<String,Object> getHeaderInputParameters(String name,boolean required,String type,Object defaultValue,String description){
        Map<String,Object> schema = new HashMap<>();
        schema.put("type",type);
        if(defaultValue != null){
            schema.put("default",defaultValue);
        }
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
   
    
    Map<String,Object> getInputParameters(String in, String name,boolean required, Map<String,Object> schema,String description ){
        Map<String,Object> ret = new HashMap<>();
        ret.put("in",in);
        ret.put("name",name);
        ret.put("schema",schema);
        ret.put("required",required);
        ret.put("description",description);
        return ret;
    }

    Map<String,Object> getJsonPostRequestBody(Object example,Map<String,Object> schema){

        Map<String,Object> appRoot = new HashMap<>();

        if(schema != null && schema.containsKey("title")){
            appRoot.put("schema",schema);
            if(schema.containsKey("title")){
                String title = schema.get("title").toString();
                JsonSchemaToModel jsonSchemaToModel = new JsonSchemaToModel(title);
                Model model = jsonSchemaToModel.convert(schema);
                ModelToJson modelToJson = new ModelToJson();
                appRoot.put("example",modelToJson.convert(model,title));
            }
        } else if(example != null) {
            appRoot.put("example",example);
        }
        Map<String,Object> applicationType = new HashMap<>();
        applicationType.put("application/json",appRoot);
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("content",applicationType);
        return requestBody;
    }
}
