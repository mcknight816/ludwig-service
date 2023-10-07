package com.bluntsoftware.ludwig.repository;


import com.bluntsoftware.ludwig.domain.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ModelRepository extends ReactiveMongoRepository<Model, String> {
  Flux<Model> findAllByOwnerIgnoreCaseContainingOrNameIgnoreCaseContainingOrderByUpdateDateDesc(String owner,String name, Pageable pageable);

  Flux<Model> findAllByOwnerAndNameIgnoreCaseContainingOrderByUpdateDateDesc(String owner,String name, Pageable pageable);
  Flux<Model> findAllByOwner(String owner);

  Mono<Model> findFirstByOwnerAndName(String owner,String modelType);

}
