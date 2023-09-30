package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.Application;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApplicationRepository extends ReactiveMongoRepository<Application, String> {
    Mono<Application> findByPath(String appPath);
}
