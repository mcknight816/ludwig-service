package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.Knowledge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface KnowledgeRepository extends ReactiveMongoRepository<Knowledge, String> {
    Flux<Knowledge> findAllByBaseId(String baseId);

    Mono<Knowledge> findAllByBaseIdAndUserId(String baseId, String userId);
}
