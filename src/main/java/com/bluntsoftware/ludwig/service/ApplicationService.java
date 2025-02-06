package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.trigger.TimerTriggerActivity;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TimerTrigger;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramTrigger;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.domain.TriggerTask;
import com.bluntsoftware.ludwig.event.*;
import com.bluntsoftware.ludwig.repository.ApplicationRepository;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service()
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher publisher;

    public ApplicationService(ApplicationRepository applicationRepository, ApplicationEventPublisher publisher) {
        this.applicationRepository = applicationRepository;
        this.publisher = publisher;
    }
    public Flux<Application> findAll() {
        return  applicationRepository.findAll();
    }

    public Mono<Application> findById(String id) {
        return applicationRepository.findById(id);
    }

    public Mono<Application> save(Application application) {

        Application app = applicationRepository.save(application).block();

        assert app != null;
        publisher.publishEvent(new AppSaveEvent(EventData.builder()
                .eventAction(EventAction.SAVE_EVENT)
                .eventSubject(EventSubject.APPLICATION)
                .subjectId(app.getId())
                .tenantId(TenantResolver.resolve())
                .eventMessage("Saving application " + app.getName())
                .build()));

        return Mono.just(app);
    }

    public Mono<Application> deleteById(String id) {
        Application app =  applicationRepository.findById(id).block();
        applicationRepository.deleteById(id).block();
        assert app != null;
        return Mono.just(app);
    }

    public Application findByPath(String appPath) {
        return applicationRepository.findByPath(appPath).block();
    }

    public Mono<List<ScheduledTask>> findAllScheduledTasks() {
        return applicationRepository.findAll()
            .flatMap(this::getScheduledTasks)
            .collectList();
    }

    public Mono<List<ScheduledTask>> scheduledTasks(Application app) {
        return getScheduledTasks(app).collectList();
    }

    private Flux<ScheduledTask> getScheduledTasks(Application app){
        return Flux.fromIterable(app.getFlows())
                .flatMap(flow -> Flux.fromIterable(flow.getActivities())
                        .filter(fa -> fa.getActivityClass().equalsIgnoreCase(TimerTriggerActivity.class.getName()))
                        .map(fa ->  ScheduledTask.builder()
                                .flowActivityId(fa.getId())
                                .flowId(flow.getId())
                                .appId(app.getId())
                                .activityClassId(TimerTriggerActivity.class.getName())
                                .tenantId(TenantResolver.resolve())
                                .name(fa.getName())
                                .active(true)
                                .cronExpression(TimerTrigger.getCronExpression(fa.getInput()))
                                .build())
                );
    }

    public Mono<List<TriggerTask>> findAllTriggeredTasks() {
        return applicationRepository.findAll()
                .flatMap(this::getTriggeredTasks)
                .collectList();
    }

    public Mono<List<TriggerTask>> triggeredTasks(Application app) {
        return getTriggeredTasks(app).collectList();
    }
    public Flux<TriggerTask> getTriggeredTasks(Application app) {
        return Flux.fromIterable(app.getFlows())
                .flatMap(flow -> Flux.fromIterable(flow.getActivities())
                        .filter(fa -> fa.getActivityClass().equalsIgnoreCase(TelegramTrigger.class.getName()))
                        .map(fa ->  TriggerTask.builder()
                                .flowActivityId(fa.getId())
                                .flowId(flow.getId())
                                .appId(app.getId())
                                .activityClassId(TelegramTrigger.class.getName())
                                .tenantId(TenantResolver.resolve())
                                .name(fa.getName())
                                .active(true)
                                .build())
                );
    }
}
