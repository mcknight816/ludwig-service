package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.model.FlowRequest;
import com.bluntsoftware.ludwig.repository.FlowRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@AllArgsConstructor
public class FlowRequestService {
    private final FlowRequestRepository flowRequestRepository;

    public Flux<FlowRequest> findAll() {
        return  flowRequestRepository.findAll();
    }

    public Mono<FlowRequest> findById(String id) {
        return flowRequestRepository.findById(id);
    }

    public Mono<FlowRequest> save(FlowRequest flowRequest) {
        return flowRequestRepository.save(flowRequest);
    }

    public Mono<FlowRequest> deleteById(String id) {
        return flowRequestRepository.findById(id).publishOn(Schedulers.boundedElastic()).map(f-> {
            flowRequestRepository.deleteById(id).block();
            return f;
        });
    }
}
