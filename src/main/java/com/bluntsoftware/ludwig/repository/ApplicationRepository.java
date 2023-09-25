package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.model.Application;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends ReactiveMongoRepository<Application, String> {
}
