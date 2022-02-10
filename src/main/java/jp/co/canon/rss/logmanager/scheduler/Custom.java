package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ReqCustomRunDTO;
import jp.co.canon.rss.logmanager.dto.job.ResMakeLogger;
import jp.co.canon.rss.logmanager.dto.job.ResStepStatusDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class Custom extends RunStepFlow implements SchedulerStrategy {
    @Override
    public void runStep(StepEntity stepEntity, boolean manual) throws ConvertException {
        // Make Logger
        ResMakeLogger resMakeLogger = makeLogger(stepEntity);
        Logger logger = resMakeLogger.getLogger();

        // Add History Table
        HistoryEntity historyEntity = addHistoryTable(stepEntity, logger, manual, resMakeLogger.getRid());

        // Get Cras Server Address
        String crasServer = getCrasServer(stepEntity, logger);

        // Set Headers
        HttpHeaders headers = setHeaders(stepEntity, logger);

        // Run Step
        CallRestAPI callRestAPI = new CallRestAPI();
        Object response = new Object();

        ReqCustomRunDTO reqRunConvertDTO = new ReqCustomRunDTO()
                .setStep(stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()))
                .setScript_type(stepEntity.getScriptType());
        HttpEntity<Object> requestConvert = new HttpEntity<>(reqRunConvertDTO, headers);

        response = callRestAPI.postRestAPI(crasServer + ReqURLController.API_POST_RUN_STEP,
                requestConvert,
                Object.class,
                ReqURLController.API_POST_RUN_STEP);
        logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_POST_RUN_STEP);
        logger.info("Step ID : " + stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()));
        logger.info("Request to execute job step");

        // Get ridCras
        String ridCras = getRidCras(response, logger);

        // Get Job Status
        String getStepStatusURL = String.format(ReqURLController.API_GET_CHECK_STEP_STATUS, ridCras);
        ResStepStatusDTO resStepStatusDTO = getJobStatus(crasServer + getStepStatusURL, ridCras, headers, historyEntity.getHistoryId(), logger);
        logger.info("Cras Server request URL : " + crasServer + getStepStatusURL);

        // Send Error Notice Mail
        if (resStepStatusDTO.getStatus().equals(RunStep.STATUS_FAILURE))
            sendErrorNotice(stepEntity, logger);

        // Update Ending Time Column of History Table
        updateEndingTime(historyEntity.getHistoryId(), logger);

        // Find Previous Step
        if(!manual)
            findPreStep(stepEntity);
    }
}
