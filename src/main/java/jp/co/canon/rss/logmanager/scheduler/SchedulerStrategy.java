package jp.co.canon.rss.logmanager.scheduler;

import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;

import java.io.IOException;

public interface SchedulerStrategy {
    void runStep(StepEntity stepEntity, boolean manual) throws ConvertException, IOException;
    void stopStep(int jobId) throws ConvertException, IOException;
}
