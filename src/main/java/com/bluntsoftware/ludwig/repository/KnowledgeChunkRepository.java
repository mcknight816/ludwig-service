package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface KnowledgeChunkRepository extends ReactiveMongoRepository<KnowledgeChunk, String>, KnowledgeChunkCustomRepository {
    Mono<Void> deleteAllByKnowledgeId(String knowledgeId);
}
