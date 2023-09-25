package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.model.Config;
import com.bluntsoftware.ludwig.repository.ConfigRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    public Flux<Config> findAll() {
        return  configRepository.findAll();
    }

    public Mono<Config> findById(String id) {
        return configRepository.findById(id);
    }

    public Mono<Config> save(Config application) {
        return configRepository.save(application);
    }

    public Mono<Config> deleteById(String id) {
        return configRepository.findById(id).publishOn(Schedulers.boundedElastic()).map(f-> {
            configRepository.deleteById(id).block();
            return f;
        });
    }

}
