package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;




@Repository
public interface KnowledgeBaseRepository extends ReactiveMongoRepository<KnowledgeBase, String>{
    Flux<KnowledgeBase> findAllByName(String name);
}
