package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.ModelJson;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ModelJsonRepository extends ReactiveMongoRepository<ModelJson, String> {
    Mono<ModelJson> findFirstByModelType(String modelType);
}
