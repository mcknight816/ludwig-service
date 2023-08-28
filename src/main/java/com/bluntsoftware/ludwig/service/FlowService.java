package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.model.Flow;
import com.bluntsoftware.ludwig.repository.FlowRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class FlowService{

  private final FlowRepo repo;

  public FlowService(FlowRepo repo) {
    this.repo = repo;
  }

  public Mono<Flow> save(Flow item) {
    return repo.save(item);
  }

  public Mono<Void> deleteById(String id) {
    return repo.deleteById(id);
  }

  public Mono<Flow> findById(String id) {
    return repo.findById(id);
  }

  public Flux<Flow> findAll() {
    return repo.findAll();
  }

  public Flux<Flow> search(String term,Pageable pageable) {
    log.info("create a filter in repo for search term {}",term);
    return repo.findAllBy(pageable);
  }

}
