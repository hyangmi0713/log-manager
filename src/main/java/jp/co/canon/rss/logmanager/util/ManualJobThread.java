package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.scheduler.Scheduler;
import jp.co.canon.rss.logmanager.scheduler.SchedulerStrategy;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class ManualJobThread implements Runnable {
    private SchedulerStrategy manualSchedulerStrategy;
    private StepEntity stepEntity;
    private ThreadPoolTaskScheduler scheduler;
    private Map<String, ScheduledFuture<?>> scheduledTasks;
    private Boolean manual;
    public ManualJobThread(SchedulerStrategy manualSchedulerStrategy, StepEntity stepEntity, ThreadPoolTaskScheduler scheduler,
                     Map<String, ScheduledFuture<?>> scheduledTasks, Boolean manual) {
        this.manualSchedulerStrategy = manualSchedulerStrategy;
        this.stepEntity = stepEntity;
        this.scheduler = scheduler;
        this.scheduledTasks = scheduledTasks;
        this.manual = manual;
    }

    @SneakyThrows
    @Override
    public void run() {
        Scheduler manualScheduler = new Scheduler(manualSchedulerStrategy, scheduler, scheduledTasks);
        manualScheduler.startManualScheduler(stepEntity, manual);
    }
}
