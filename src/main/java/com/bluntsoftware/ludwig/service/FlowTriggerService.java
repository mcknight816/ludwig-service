package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.service.Trigger;
import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.domain.TriggerTask;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.saasy.repository.TenantRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.publisher.Mono;

import java.util.*;


@Slf4j
//@Service
public class FlowTriggerService implements InitializingBean, DisposableBean {

    private final TenantRepo tenantRepository;
    private final ApplicationService applicationService;
    private final FlowRunnerService flowRunnerService;
    private final ActivityRepository activityRepository;

    // A list of all active triggers.
    private final List<Trigger<?>> triggers = new ArrayList<>();

    //private final Map<String, Trigger<?>> triggers = new HashMap<>();


    public FlowTriggerService(TenantRepo tenantRepository, ApplicationService applicationService, FlowRunnerService flowRunnerService, ActivityRepository activityRepository) {
        this.tenantRepository = tenantRepository;
        this.applicationService = applicationService;
        this.flowRunnerService = flowRunnerService;
        this.activityRepository = activityRepository;
    }

    @Override
    public void afterPropertiesSet() {
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block())
                .forEach(tenant ->{
                    TenantResolver.setCurrentTenant(tenant.getId());
                    List<TriggerTask> tasks = applicationService.findAllTriggeredTasks().block();
                    assert tasks != null;
                    for (TriggerTask task : tasks) {
                        task.setTenantId(tenant.getId());
                    }
                });
        TenantResolver.setCurrentTenant(currentTenantId);

        // Initialize Telegram Bot triggers dynamically.
        /*if (flowTriggerProperties.getTelegramBots() != null) {
            flowTriggerProperties.getTelegramBots().forEach(botConfig -> {
                Trigger<?> telegramTrigger = new TelegramBotTrigger(botConfig);
                telegramTrigger.start();
                triggers.add(telegramTrigger);
            });
        }

        // Initialize Kafka Consumer triggers dynamically.
        if (flowTriggerProperties.getKafkaConsumers() != null) {
            flowTriggerProperties.getKafkaConsumers().forEach(kafkaConfig -> {
                Trigger<?> kafkaTrigger = new KafkaConsumerTrigger(kafkaConfig);
                kafkaTrigger.start();
                triggers.add(kafkaTrigger);
            });
        }

         */

        log.info("FlowTriggerService initialized {} triggers", triggers.size());
    }

    @Override
    public void destroy() {
        log.info("Shutting down FlowTriggerService and all triggers...");
        triggers.forEach(Trigger::stop);
    }
}
