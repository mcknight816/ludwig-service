package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.repository.KnowledgeBaseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class KnowledgeBaseService {
    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public KnowledgeBaseService(KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    public Mono<KnowledgeBase> save(KnowledgeBase knowledgeBase) {
        return knowledgeBaseRepository.save(knowledgeBase);
    }

    public Mono<KnowledgeBase> findById(String id) {
        return knowledgeBaseRepository.findById(id);
    }

    public Flux<KnowledgeBase> findAll() {
        return knowledgeBaseRepository.findAll();
    }

    public Mono<Void> deleteById(String id) {
        return knowledgeBaseRepository.deleteById(id);
    }
}
