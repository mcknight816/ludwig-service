package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface KnowledgeBaseRepository extends ReactiveMongoRepository<KnowledgeBase, String>{
    Flux<KnowledgeBase> findAllByName(String name);

    Flux<KnowledgeBase> findAllByCategory(String category);

    Mono<KnowledgeBase> findFirstByCategory(String category);

    Mono<KnowledgeBase> findFirstByName(String name);

    Mono<KnowledgeBase> findFirstByCategoryAndUserId(String category, String userId);
}
