package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.model.Application;
import com.bluntsoftware.ludwig.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service()
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    public Flux<Application> findAll() {
        return  applicationRepository.findAll();
    }

    public Mono<Application> findById(String id) {
        return applicationRepository.findById(id);
    }

    public Mono<Application> save(Application application) {
        return applicationRepository.save(application);
    }

    public Mono<Application> deleteById(String id) {
        return applicationRepository.findById(id).publishOn(Schedulers.boundedElastic()).map(f-> {
            applicationRepository.deleteById(id).block();
            return f;
        });
    }
}