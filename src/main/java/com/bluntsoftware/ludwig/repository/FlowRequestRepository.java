package com.bluntsoftware.ludwig.repository;


import com.bluntsoftware.ludwig.domain.FlowRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRequestRepository extends ReactiveMongoRepository<FlowRequest, String> {
}
