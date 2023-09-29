package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.FlowConfig;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlowConfigService {
    private final FlowConfigRepository configRepository;
    public FlowConfigService(FlowConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    public Flux<FlowConfig> findAll() {
        return configRepository.findAll();
    }

    public Mono<FlowConfig> findById(String id) {
        return configRepository.findById(id);
    }

    public Mono<FlowConfig> save(FlowConfig application) {
        return configRepository.save(application);
    }

    public Mono<FlowConfig> deleteById(String id) {
        FlowConfig flowConfig = configRepository.findById(id).block();
        configRepository.deleteById(id).block();
        return  Mono.just(flowConfig);
    }

}
