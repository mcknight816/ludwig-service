package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.service.KnowledgeBaseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/core/knowledge-base")
public class KnowledgeBaseController {
    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<KnowledgeBase> save(@RequestBody KnowledgeBase knowledgeBase){
        return this.knowledgeBaseService.save(knowledgeBase);
    }

    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<KnowledgeBase> findById(@PathVariable("id") String id ){
        return this.knowledgeBaseService.findById(String.valueOf(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<KnowledgeBase> findAll(){
        return this.knowledgeBaseService.findAll();
    }

    @DeleteMapping(value = "{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id ){
        return this.knowledgeBaseService.deleteById(String.valueOf(id));
    }

}
