package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.Application;
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
        Application app =  applicationRepository.findById(id).block();
        applicationRepository.deleteById(id).block();
        return Mono.just(app);
    }

    public Application findByPath(String appPath) {
        return applicationRepository.findByPath(appPath).block();
    }
}
