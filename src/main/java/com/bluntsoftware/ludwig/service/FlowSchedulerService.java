package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.input.InputActivity;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.event.AppSaveEvent;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.saasy.repository.TenantRepo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class FlowSchedulerService {
    //Should we rely on tenant repo ?
    private final TenantRepo tenantRepository;
    private final ApplicationService applicationService;
    private final FlowRunnerService flowRunnerService;
    private final ActivityRepository activityRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    public FlowSchedulerService(TenantRepo tenantRepository, ApplicationService applicationService, FlowRunnerService flowRunnerService, ActivityRepository activityRepository, ThreadPoolTaskScheduler taskScheduler) {
        this.tenantRepository = tenantRepository;
        this.applicationService = applicationService;
        this.flowRunnerService = flowRunnerService;
        this.activityRepository = activityRepository;
        this.taskScheduler = taskScheduler;
    }
    @EventListener
    void listenForAppChanges(@NotNull AppSaveEvent event) {
         Objects.requireNonNull(applicationService.scheduledTasks(applicationService
                         .findById(event.getEventData().getSubjectId()).block())
                 .block()).forEach(this::updateTask);
    }

    @PostConstruct
    public void initializeSchedulers() {
        taskScheduler.initialize();
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block())
                .forEach(tenant ->{
                    TenantResolver.setCurrentTenant(tenant.getId());
                    List<ScheduledTask> tasks = applicationService.findAllScheduledTasks().block();
                    assert tasks != null;
                    for (ScheduledTask task : tasks) {
                        task.setTenantId(tenant.getId());
                        scheduleTask(task);
                    }
                });
        TenantResolver.setCurrentTenant(currentTenantId);
    }

    public void scheduleTask(ScheduledTask task) {
        if (task.isActive() && task.getCronExpression() != null) {
            Runnable runnableTask = createTask(task);
            scheduledTasks.put(getTaskId(task),taskScheduler.schedule(runnableTask, new CronTrigger(task.getCronExpression())));
            System.out.println("Scheduled task: " + task.getName());
        }
    }

    String getTaskId(ScheduledTask task){
        return task.getTenantId() + "-" + task.getAppId() + "-" + task.getFlowId() + "-" + task.getFlowActivityId();
    }

    public void updateTask(ScheduledTask task) {
        // Cancel existing task
        cancelTask(getTaskId(task));

        // Re-schedule task if still active
        if (task.isActive() && task.getCronExpression() != null) {
            scheduleTask(task);
        }
    }

    public void cancelTask(String taskId) {
        if (scheduledTasks.containsKey(taskId)) {
            scheduledTasks.get(taskId).cancel(false);
            // Remove the task from the scheduler
            scheduledTasks.remove(taskId);
            System.out.println("Canceled task with ID: " + taskId);
        }
    }

    private Runnable createTask(ScheduledTask task) {
        return () -> {
            Application application = this.applicationService.findById(task.getAppId()).block();
            InputActivity activity = (InputActivity)activityRepository.getByKlass(task.getActivityClassId());
            Map<String,Object> in = new HashMap<>();
            assert application != null;
            Flow flow  = application.getFlows()
                    .stream()
                    .filter(f-> f.getId().equals(task.getFlowId()))
                    .findFirst()
                            .orElse(null);
            assert flow != null;
            log.info("Executing scheduled Request to {} for flow {}", application.getName(), flow.getName());
            flowRunnerService.runFlowWithActivityInputAndContext(flow,activity,in,null);
        };
    }
}
