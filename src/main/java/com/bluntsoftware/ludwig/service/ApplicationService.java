package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.input.TimerActivity;
import com.bluntsoftware.ludwig.conduit.activities.input.domain.TimerInput;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.repository.ApplicationRepository;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

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

    public Mono<List<ScheduledTask>> findAllScheduledTasks() {
        return applicationRepository.findAll()
            .flatMap(app -> Flux.fromIterable(app.getFlows())
                .flatMap(flow -> Flux.fromIterable(flow.getActivities())
                    .filter(fa -> fa.getActivityClass().equalsIgnoreCase(TimerActivity.class.getName()))
                    .map(fa ->  ScheduledTask.builder()
                            .flowActivityId(fa.getId())
                            .flowId(flow.getId())
                            .tenantId(TenantResolver.resolve())
                            .name(fa.getName())
                            .active(true)
                            .cronExpression(TimerInput.getCronExpression(fa.getInput()))
                            .build())
                )
            )
            .collectList();
    }



}
