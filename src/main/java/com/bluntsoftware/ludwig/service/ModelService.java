package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.domain.ModelJson;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ModelService {
    Mono<Model> save(Model model);
    Mono<Model> findById(String id);
    Mono<Void>  deleteById(String id);
    Flux<Model> search(String searchString, Pageable pageable);
    Flux<Model> findAll();
    List<Entity> importFromJson(String name, Map<String, Object> json);
    List<Entity> importFromYaml(String yaml);

    File download(Model model, String type);
    Flux<Model> systemModels();
    Mono<Model> getOrCreateSystemModel(String modelType);
    Mono<ModelJson> generateAIModelTypeJson(String modelType);
    Flux<Model> findAllByName(String searchString, Pageable pageable);
}
