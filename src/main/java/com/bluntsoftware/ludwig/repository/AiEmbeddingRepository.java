package com.bluntsoftware.ludwig.repository;


import com.bluntsoftware.ludwig.domain.AiEmbedding;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;


@Repository
public interface AiEmbeddingRepository extends ReactiveMongoRepository<AiEmbedding, String> {



    Flux<AiEmbedding> getAiEmbeddingByUserIdAndCategory(String userId, String category);
}
