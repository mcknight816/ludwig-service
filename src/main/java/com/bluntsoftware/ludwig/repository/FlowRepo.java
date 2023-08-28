package com.bluntsoftware.ludwig.repository;

import reactor.core.publisher.Flux;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.bluntsoftware.ludwig.model.Flow;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepo extends ReactiveMongoRepository<Flow, String> {

	Flux<Flow> findAllBy(Pageable pageable);
}