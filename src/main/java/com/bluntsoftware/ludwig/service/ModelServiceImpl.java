package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.ai.AIService;
import com.bluntsoftware.ludwig.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.ai.domain.AICompletionResponse;
import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.ModelJson;
import com.bluntsoftware.ludwig.repository.ModelJsonRepository;
import com.bluntsoftware.ludwig.repository.ModelRepository;
import com.bluntsoftware.ludwig.utils.Inflector;
import com.bluntsoftware.ludwig.utils.converter.ConverterFactory;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonToModel;
import com.bluntsoftware.ludwig.utils.converter.impl.ModelToJsonSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository repo;


    private final ObjectMapper objectMapper;

    private final ModelJsonRepository modelJsonRepository;

    public ModelServiceImpl(ModelRepository modelDao,   ObjectMapper objectMapper, ModelJsonRepository modelJsonRepository) {
      this.repo = modelDao;
      this.objectMapper = objectMapper;
      this.modelJsonRepository = modelJsonRepository;
    }

    @Override
    public Mono<Model> save(Model model) {


        model.setUpdateDate(Instant.now());
        if(model.getCreateDate() == null){
            model.setCreateDate(Instant.now());
        }
        return repo.save(model);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repo.deleteById(id);
    }

    @Override
    public Mono<Model> findById(String id) {
        return repo.findById(id);
    }

    public Flux<Model> search(String searchString, Pageable pageable){
      return  repo.findAllByOwnerIgnoreCaseContainingOrNameIgnoreCaseContainingOrderByUpdateDateDesc(searchString,searchString,pageable);

    }

    @Override
    public Flux<Model> findAll() {
      return repo.findAll();
    }

    @Override
    public List<Entity> importFromJson(String name, Map<String, Object> json) {
      return  ConverterFactory.buildEntities(name,json);
    }

  @Override
  public List<Entity> importFromYaml(String yaml) {
    ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
    yamlReader.findAndRegisterModules();
    try {
      return  JsonToModel.buildFromMap(yamlReader.readValue(yaml, ConcurrentModel.class));
    } catch (JsonProcessingException jsonProcessingException) {
      jsonProcessingException.printStackTrace();
    }
    return Collections.emptyList();
  }

    @Override
    public File download(Model model, String type) {
        return null;//new SpringBootJpaGenerator().zip(model, type);
    }

 static String  exportYaml(Map<String,Object> model) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    mapper.findAndRegisterModules();
    return mapper.writeValueAsString(model);
  }
  public Flux<Model> systemModels(){
    return repo.findAllByOwner("system");
  }

  public Mono<Model> getOrCreateSystemModel(String modelType)  {

     Model ret =repo.findFirstByOwnerAndName("system",modelType).block();

    if(ret == null){
        try {
            ret  = this.generateModelType(modelType);

            return save(ret);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    return Mono.just(ret);
  }

    @Override
    public Flux<Model> findAllByName(String name, Pageable pageable) {
        return repo.findAllByOwnerAndNameIgnoreCaseContainingOrderByUpdateDateDesc("system",name,pageable);
    }

    @Override
    public Map<String, Object> entityToSchema(String entityName, List<Entity> model) {
        ModelToJsonSchema modelToJsonSchema = new ModelToJsonSchema();
        return modelToJsonSchema.convert(Model.builder().entities(model).build(),entityName);
    }


    public Mono<ModelJson> generateAIModelTypeJson(String modelType)  {
        String title = Inflector.getInstance().titleCase(modelType
                        .toLowerCase())
                        .trim()
                        .replace(" ","")
                        .replaceAll("[^a-zA-Z0-9]", "");

         ModelJson modelJson = modelJsonRepository.findFirstByModelType(title).block();
        if(modelJson == null){
            AICompletionResponse completionResponse = AIService.completions(new AICompletionRequest().toBuilder().build());
                    //.prompt("build a complex json model with double quoted lowercase fields for a " + modelType).build());

            ConcurrentModel json;
            try {
                json = objectMapper.readValue(completionResponse.getChoices().get(0).getMessage().getContent(), ConcurrentModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(!json.contains("id")){
                json.put("id","123445");
            }

            return modelJsonRepository.save(ModelJson.builder()
                    .json(json)
                    .modelType(title)
                    .build());
        }
        return Mono.just(modelJson);
    }
    public Model generateModelType(String modelType) throws JsonProcessingException {

        ModelJson modelJson = generateAIModelTypeJson(modelType).block();

        return Model.builder()
                .name(modelType)
                .owner("system")
                .description("")
                .entities(ConverterFactory.buildEntities(modelJson.getModelType(),modelJson.getJson())).build();
    }

  public static void main(String[] args) {
    Map<String,Object>  data = new HashMap<>();
    data.put("test","test");
    try {
      System.out.println(exportYaml(data));
    } catch (JsonProcessingException jsonProcessingException) {
      jsonProcessingException.printStackTrace();
    }
  }
}
