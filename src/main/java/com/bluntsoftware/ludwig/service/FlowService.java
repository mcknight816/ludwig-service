package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.repository.FlowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class FlowService{

  private final FlowRepository flowRepository;

  public FlowService(FlowRepository flowRepository) {
    this.flowRepository = flowRepository;
  }

  public Mono<Flow> save(Flow item) {
    Flow flow = flowRepository.getByName(item.getName()).block();
    if(flow != null && !flow.getId().equalsIgnoreCase(item.getId())){
      throw new RuntimeException("flow name already exists");
    }
    return flowRepository.save(item);
  }

  public Mono<Void> deleteById(String id) {
    return flowRepository.deleteById(id);
  }

  public Mono<Flow> findById(String id) {
    return flowRepository.findById(id);
  }

  public Flux<Flow> findAll() {
    return flowRepository.findAll();
  }

  public Flux<Flow> search(String term,Pageable pageable) {
    log.info("create a filter in repo for search term {}",term);
    return flowRepository.findAllBy(pageable);
  }
}
