package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.conduit.activities.input.InputActivity;
import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.ScheduledTask;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
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
public class FlowSchedulerService {
    //Should we rely on tenant repo ?
    private final TenantRepo tenantRepository;
    private final ApplicationService applicationService;
    private final FlowRunnerService flowRunnerService;
    private final ActivityRepository activityRepository;

    private final TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final Map<String, Runnable> scheduledTasks = new HashMap<>();

    public FlowSchedulerService(TenantRepo tenantRepository, ApplicationService applicationService, FlowRunnerService flowRunnerService, ActivityRepository activityRepository ) {
        this.tenantRepository = tenantRepository;
        this.applicationService = applicationService;
        this.flowRunnerService = flowRunnerService;
        this.activityRepository = activityRepository;
    }

    public void refreshSchedulers(){
        ((ThreadPoolTaskScheduler) taskScheduler).initialize();
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block()).forEach(t->{
            TenantResolver.setCurrentTenant(t.getId());
            List<ScheduledTask> tasks = applicationService.findAllScheduledTasks().block();
            assert tasks != null;
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
