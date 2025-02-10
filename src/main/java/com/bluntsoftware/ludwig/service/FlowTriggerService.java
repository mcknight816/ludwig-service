package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.Activity;
import com.bluntsoftware.ludwig.conduit.activities.trigger.TelegramTriggerActivity;
import com.bluntsoftware.ludwig.conduit.activities.trigger.domain.TelegramTrigger;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.service.Trigger;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.TriggerTask;
import com.bluntsoftware.ludwig.event.AppSaveEvent;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBotService;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBotTrigger;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FlowTriggerService implements DisposableBean {

    private final ObjectMapper objectMapper;
    private final TenantRepo tenantRepository;
    private final ApplicationService applicationService;
    private final FlowRunnerService flowRunnerService;
    private final ActivityRepository activityRepository;
    private final ActivityConfigRepository activityConfigRepository;
    private final TelegramBotService telegramBotService;
    private final Map<String,Trigger<?>> triggers = new ConcurrentHashMap<>();

    public FlowTriggerService(ObjectMapper objectMapper, TenantRepo tenantRepository, ApplicationService applicationService, FlowRunnerService flowRunnerService, ActivityRepository activityRepository, ActivityConfigRepository activityConfigRepository, TelegramBotService telegramBotService) {
        this.objectMapper = objectMapper;
        this.tenantRepository = tenantRepository;
        this.applicationService = applicationService;
        this.flowRunnerService = flowRunnerService;
        this.activityRepository = activityRepository;
        this.activityConfigRepository = activityConfigRepository;
        this.telegramBotService = telegramBotService;
    }
    @EventListener
    void listenForAppChanges(@NotNull AppSaveEvent event) {
        Objects.requireNonNull(applicationService.triggeredTasks(applicationService
                        .findById(event.getEventData().getSubjectId()).block())
                .block()).forEach(this::updateTask);
    }

    @PostConstruct
    public void initializeTriggers() {
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block())
                .forEach(tenant ->{
                    TenantResolver.setCurrentTenant(tenant.getId());
                    List<TriggerTask> tasks = applicationService.findAllTriggeredTasks().block();
                    assert tasks != null;
                    for (TriggerTask task : tasks) {
                        task.setTenantId(tenant.getId());
                        startTask(task);
                    }
                });
        TenantResolver.setCurrentTenant(currentTenantId);
        log.info("FlowTriggerService initialized {} triggers", triggers.size());
    }

    private void createTelegramTask(TriggerTask task){
        TelegramTrigger telegramTrigger = objectMapper.convertValue(task.getInput(),TelegramTrigger.class);
        TelegramConfig config = activityConfigRepository.getConfigByNameAs(telegramTrigger.getConfig(), TelegramConfig.class);
        TelegramBotTrigger trigger = new TelegramBotTrigger(telegramBotService,config, update -> {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String receivedText = update.getMessage().getText();
                telegramTrigger.setText(receivedText);
                telegramTrigger.setChatId(chatId);
                task.setInput(objectMapper.convertValue(telegramTrigger,Map.class));
                triggerActivityTask(task);
                log.info("Trigger executed for task: {}", task);
            }
        });
        triggers.put(getTaskId(task),trigger);
    }

    private void createTask(TriggerTask task){
        String currentTenantId = TenantResolver.resolve();
        TenantResolver.setCurrentTenant(task.getTenantId());
        //Create a Telegram Task
        if(TelegramTriggerActivity.class.getName().equalsIgnoreCase(task.getActivityClassId())){
            createTelegramTask(task);
        }
        TenantResolver.setCurrentTenant(currentTenantId);
    }

    String getTaskId(TriggerTask task){
        return task.getTenantId() + "-" + task.getAppId() + "-" + task.getFlowId() + "-" + task.getFlowActivityId();
    }

    void triggerActivityTask(TriggerTask task){
        String currentTenantId = TenantResolver.resolve();
        TenantResolver.setCurrentTenant(task.getTenantId());
        Application application = this.applicationService.findById(task.getAppId()).block();
        Activity activity = activityRepository.getByKlass(task.getActivityClassId());
        assert application != null;
        Flow flow  = application.getFlows()
                .stream()
                .filter(f-> f.getId().equals(task.getFlowId()))
                .findFirst()
                .orElse(null);
        assert flow != null;
        log.info("Executing scheduled Request to {} for flow {}", application.getName(), flow.getName());
        flowRunnerService.runFlowWithActivityInputAndContext(flow,activity,task.getInput(),null);
        TenantResolver.setCurrentTenant(currentTenantId);
    }



    public void updateTask(TriggerTask task) {
        // Cancel existing task
        cancelTask(getTaskId(task));
        // Re-schedule task if still active
        if (task.isActive()) {
            startTask(task);
        }
    }

    void cancelTask(String taskId){
        if (triggers.containsKey(taskId)) {
            triggers.get(taskId).stop();
            // Remove the task from the scheduler
            triggers.remove(taskId);
            System.out.println("Canceled task with ID: " + taskId);
        }
    }

    private void startTask(TriggerTask task) {
        String triggerId = getTaskId(task);
        if(!triggers.containsKey(triggerId)){
            createTask(task);
        }
        log.info("Starting Trigger Task {}",task);
        triggers.get(triggerId).start();
    }

    @Override
    public void destroy() {
        log.info("Shutting down FlowTriggerService and all triggers...");
        triggers.forEach((k,t) -> cancelTask(k));
    }
}
