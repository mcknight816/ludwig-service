package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.repository.KnowledgeChunkRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class KnowledgeService {
    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;

    public KnowledgeService(KnowledgeRepository knowledgeRepository, KnowledgeChunkRepository knowledgeChunkRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
    }
    public Mono<Knowledge> save(Knowledge knowledge) {



        return knowledgeRepository.save(knowledge);
    }

    public Mono<Knowledge> findById(String id) {
        return knowledgeRepository.findById(id);
    }

    public Flux<Knowledge> findAll() {
        return knowledgeRepository.findAll();
    }

    public Mono<Void> deleteById(String id) {
        return knowledgeRepository.deleteById(id);
    }

    public Flux<Knowledge> findAllByBaseId(String s) {
        return knowledgeRepository.findAllByBaseId(s);
    }
}
