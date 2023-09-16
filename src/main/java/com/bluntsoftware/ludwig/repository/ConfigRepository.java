package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.model.Config;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends ReactiveMongoRepository<Config, String> {
}
