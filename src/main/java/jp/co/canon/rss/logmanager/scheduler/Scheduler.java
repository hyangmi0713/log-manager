package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ResMakeLogger;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.util.FileLog;
import jp.co.canon.rss.logmanager.util.RandomString;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class Scheduler {
    private final SchedulerStrategy schedulerStrategy;
    private final ThreadPoolTaskScheduler scheduler;
    private Map<String, ScheduledFuture<?>> scheduledTasks;
    public Scheduler(SchedulerStrategy schedulerStrategy, ThreadPoolTaskScheduler scheduler,
                     Map<String, ScheduledFuture<?>> scheduledTasks) {
        this.schedulerStrategy = schedulerStrategy;
        this.scheduler = scheduler;
        this.scheduledTasks = scheduledTasks;
    }

    public void startScheduler(StepEntity stepEntity) throws ConvertException, IOException {
        // Create a scheduler
        switch (stepEntity.getMode()) {
            case(RunStep.MODE_CYCLE):
                long period = 0;

                switch (stepEntity.getCycle()) {
                    case (RunStep.CYCLE_MINUTE) :
                        period = stepEntity.getPeriod() * 60 * 1000;
                        break;
                    case (RunStep.CYCLE_HOUR) :
                        period = stepEntity.getPeriod() * 60 * 60 * 1000;
                        break;
                    case (RunStep.CYCLE_DAY) :
                        period = stepEntity.getPeriod() * 24 * 60 * 60 * 1000;
                        break;
                }

                ScheduledFuture<?> taskScheduleAtFixedRate = scheduler.scheduleAtFixedRate(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        schedulerStrategy.runStep(stepEntity, false);
                    }
                }, period);
                log.info("create scheduler : " + stepEntity.getStepType() + " / " + stepEntity.getCycle() + " / " + stepEntity.getPeriod());
                scheduledTasks.put(stepEntity.getStepType() + "_" + stepEntity.getUuid(), taskScheduleAtFixedRate);
                break;
            case(RunStep.MODE_TIME):
                for(String cron : stepEntity.getCron()) {
                    ScheduledFuture<?> taskSchedule = scheduler.schedule(new Runnable() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            schedulerStrategy.runStep(stepEntity, false);
                        }
                    }, getTrigger(cron));
                    log.info("create scheduler : " + stepEntity.getStepType() + " / " + cron);
                    scheduledTasks.put(stepEntity.getStepType() + "_" + stepEntity.getUuid(), taskSchedule);
                }
                break;
            default:
                break;
        }
    }

    public Trigger getTrigger(String cron) {
        return new CronTrigger(cron);
    }

    public void stopScheduler(JobEntity jobEntity) throws InterruptedException, ConvertException, IOException {
        schedulerStrategy.stopStep(jobEntity.getJobId());

        for(StepEntity stepEntity : jobEntity.getSteps()) {
            String taskName = stepEntity.getStepType() + "_" + stepEntity.getUuid();
            if(scheduledTasks.get(taskName) != null) {
                scheduledTasks.get(taskName).cancel(true);
                scheduledTasks.remove(taskName);
                log.info("task canceled : " + taskName);
            }
        }
    }

    public void startManualScheduler(StepEntity stepEntity, Boolean manual) throws ConvertException, IOException {
        schedulerStrategy.runStep(stepEntity, manual);
    }
}
