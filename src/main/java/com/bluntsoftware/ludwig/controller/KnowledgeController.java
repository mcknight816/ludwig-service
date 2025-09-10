package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.service.KnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@RestController
@RequestMapping("/core/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Knowledge> save(@RequestBody Knowledge knowledgeBase) {
        return this.knowledgeService.save(knowledgeBase);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Knowledge> findById(@PathVariable("id") String id) {
        return this.knowledgeService.findById(String.valueOf(id));
    }

    @GetMapping(value = "/search/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Knowledge> findAllByBaseId(@PathVariable("id") String id) {
        return this.knowledgeService.findAllByBaseId(String.valueOf(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Knowledge> findAll() {
        return this.knowledgeService.findAll();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable String id) {
         knowledgeService.deleteById(id)
                .doOnSubscribe(s -> log.info("Deleting Knowledge {}", id)).block();
         return Mono.empty();
    }

}