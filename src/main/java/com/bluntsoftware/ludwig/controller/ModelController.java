package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.ModelJson;
import com.bluntsoftware.ludwig.service.ModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/core/model")
@CrossOrigin
public class ModelController {

    private final ModelService service;
    private final ObjectMapper objectMapper;
    public ModelController(  ModelService service, ObjectMapper objectMapper) {

        this.service = service;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value="",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Model> save(@RequestBody Model model){
        return this.service.save(model);
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Model> findById(@PathVariable("id") String id ){
        return this.service.findById(id);
    }

    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Model> findAll(){
        return this.service.findAll();
    }

    @GetMapping(value ={ "/findAllByName/{name}", "findAllByName"},produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Model> findAllByName(@PathVariable(value = "name",required= false) String name ){
        if(name == null){ name = "";}
        return this.service.findAllByName(name,PageRequest.of(0,10));
    }

    @GetMapping(value = "/system",produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Model> findAllSystemModels(){
        return this.service.systemModels();
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id ){
          return this.service.deleteById(id);
    }


  @ResponseBody
  @GetMapping(value = { "/search/{search-term}", "/search"}, produces = { "application/json" })
  public Flux<Model> findAll(@PathVariable(value = "search-term",required= false) String searchTerm,
                               @RequestParam(value = "page",required= false)  Integer page,
                             @RequestParam(value = "limit",required= false)  Integer limit){
      if(searchTerm == null){ searchTerm = "";}
      if(page == null){ page = 0;}
      if(limit == null){ limit = 50;}
      Pageable pageable = PageRequest.of(page,limit);
      return this.service.search(searchTerm,pageable);
  }

  @PostMapping(value="/import",produces = MediaType.APPLICATION_JSON_VALUE)
  public  List<Entity> fromJson(@RequestParam("name") String name, @RequestBody Map<String,Object> json){
    return this.service.importFromJson(name,json);
  }

  @GetMapping(value = "/export/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<byte[]> exportModel(@PathVariable("id") String id) throws JsonProcessingException {
       Model model = this.service.findById(id).block();
       byte[] modelJsonBytes = objectMapper.writeValueAsString(model).getBytes();
       return ResponseEntity
              .ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + model.getName() + ".json")
              .contentType(MediaType.APPLICATION_JSON)
              .contentLength(modelJsonBytes.length)
              .body(modelJsonBytes);
  }

  @PostMapping(value="/import-yaml", produces = MediaType.APPLICATION_JSON_VALUE)
  public  List<Entity> fromYaml(@RequestBody Map<String,Object> yaml){
    return this.service.importFromYaml(yaml.get("yaml").toString());
  }

    @PostMapping(value="/entity-to-schema/{entity-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Map<String,Object> modelToSchema(@PathVariable("entity-name") String entityName,@RequestBody List<Entity> model){
        return this.service.entityToSchema(entityName,model);
    }


/*  @PostMapping(value="/model/publish/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public  Mono<PublicModel> publish(@PathVariable("id") String id, @RequestBody Map<String,Object> props){
    return publicService.publish(service.findById(id));
  }*/

    @PostMapping(value = "/download/{type}", produces = "application/zip", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FileSystemResource downloadModel(@PathVariable("type") String type,@RequestBody Model model, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "attachment; filename=" + model.getName() + ".zip");
        return new FileSystemResource(this.service.download(model,type));
    }

    @PostMapping(value="/upload")
    public Mono<Model> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        Model model = objectMapper.readValue(file.getInputStream(),Model.class);
        if(model == null || model.getEntities() == null || model.getEntities().size() < 1){
            throw new RuntimeException("Not valid or empty Model");
        }
        model.setId(null);
        return this.service.save(model);
    }

    @GetMapping(value="/create-from-ai/{modelType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ModelJson> fromAi(@PathVariable("modelType") String modelType){
       return this.service.generateAIModelTypeJson(modelType);
    }




}
