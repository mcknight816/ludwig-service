package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.saasy.repository.TenantRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DynamicSchedulerService {
    private final TenantRepo tenantRepository;
    private final ApplicationService applicationService;
    private final TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final Map<String, Runnable> scheduledTasks = new HashMap<>();

    public DynamicSchedulerService(TenantRepo tenantRepository, ApplicationService applicationService) {
        this.tenantRepository = tenantRepository;
        this.applicationService = applicationService;
    }

    public void refreshSchedulers(){
        ((ThreadPoolTaskScheduler) taskScheduler).initialize();
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block()).forEach(t->{
            TenantResolver.setCurrentTenant(t.getId());
            List<ScheduledTask> tasks = applicationService.findAllScheduledTasks().block();
            for (ScheduledTask task : tasks) {
                task.setTenantId(t.getId());
                scheduleTask(task);
            }
        });
        TenantResolver.setCurrentTenant(currentTenantId);
    }

    @PostConstruct
    public void initializeSchedulers() {
        this.refreshSchedulers();
    }

    public void scheduleTask(ScheduledTask task) {
        if (task.isActive() && task.getCronExpression() != null) {
            Runnable runnableTask = createTask(task);
            taskScheduler.schedule(runnableTask, new CronTrigger(task.getCronExpression()));
            scheduledTasks.put(task.getFlowActivityId(), runnableTask);
            System.out.println("Scheduled task: " + task.getName());
        }
    }

    public void updateTask(ScheduledTask task) {
        // Cancel existing task
        cancelTask(task.getFlowActivityId());

        // Re-schedule task if still active
        if (task.isActive() && task.getCronExpression() != null) {
            scheduleTask(task);
        }
    }

    public void cancelTask(String taskId) {
        if (scheduledTasks.containsKey(taskId)) {
            // Remove the task from the scheduler
            scheduledTasks.remove(taskId);
            System.out.println("Canceled task with ID: " + taskId);
        }
    }

    private Runnable createTask(ScheduledTask task) {
        return () -> {
            //System.out.println("Executing task: " + task.getTenantId() + " at " + System.currentTimeMillis());
            log.info("Task: {}",task);
        };
    }


}
