package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.model.FlowConfig;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface FlowConfigRepository extends ReactiveMongoRepository<FlowConfig, String> {
    Mono<FlowConfig> findByNameAndConfigClass(String name, String configCLass);
}
