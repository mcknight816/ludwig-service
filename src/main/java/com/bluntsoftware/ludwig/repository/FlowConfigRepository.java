package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.model.FlowConfig;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowConfigRepository extends ReactiveMongoRepository<FlowConfig, String> {
}
