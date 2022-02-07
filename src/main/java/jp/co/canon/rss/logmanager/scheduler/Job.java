package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ReqJobRunDTO;
import jp.co.canon.rss.logmanager.dto.job.ResMakeLogger;
import jp.co.canon.rss.logmanager.dto.job.ResStepStatusDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.repository.LocalJobFileEntityRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.LocalJobFileEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class Job extends RunStepFlow implements SchedulerStrategy {
    @Value("${file.upload-dir}")
    private String uploadPath;

    @Autowired
    private LocalJobFileEntityRepository localJobFileEntityRepository;

    @Override
    public void runStep(StepEntity stepEntity, boolean manual) throws ConvertException, IOException {
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

        if (stepEntity.getJobEntity().getType().equals(RunStep.TYPE_REMOTE)) {
            ReqJobRunDTO reqRunConvertDTO = new ReqJobRunDTO()
                    .setStep(stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()))
                    .setPlan_id(stepEntity.getJobEntity().getPlanIds());
            HttpEntity<Object> requestConvert = new HttpEntity<>(reqRunConvertDTO, headers);

            response = callRestAPI.postRestAPI(crasServer + ReqURLController.API_POST_RUN_STEP,
                    requestConvert,
                    Object.class,
                    ReqURLController.API_POST_RUN_STEP);
            logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_POST_RUN_STEP);
            logger.info("Step ID : " + stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()));
            logger.info("Request to execute job step");
        } else if (stepEntity.getJobEntity().getType().equals(RunStep.TYPE_LOCAL)) {
            int[] fileIndices = stepEntity.getFileIndices();
            List<String> fileNameList = new ArrayList<>();

            for (int fileInx : fileIndices) {
                Optional<LocalJobFileEntity> localJobFileEntity = localJobFileEntityRepository.findById(fileInx);
                fileNameList.add(uploadPath + File.separator + localJobFileEntity.get().getFileName());
            }

            HttpHeaders headersLocal = new HttpHeaders();
            headersLocal.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_FILE);
            headersLocal.set(ReqURLController.JOB_CLIENT_ID, stepEntity.getJobEntity().getType() + "_" + String.format("%06d", stepEntity.getJobId()));
            logger.info("client id : " + stepEntity.getJobEntity().getType() + "_" + String.format("%06d", stepEntity.getJobId()));

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            for (String file : fileNameList)
                builder.part("files", new FileSystemResource(file));

            Map<String, String> data = new HashMap<>();
            data.put("step", stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()));

            builder.part("data", data).header(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);

            MultiValueMap<String, HttpEntity<?>> body = builder.build();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headersLocal);

            ResponseEntity<?> crasReturn = callRestAPI.postWithCustomHeaderMultipartFile(
                    crasServer + ReqURLController.API_POST_RUN_STEP,
                    requestEntity,
                    Object.class);

            response = crasReturn.getBody();

            logger.info("Cras Server request URL : " + crasServer + ReqURLController.API_POST_RUN_STEP);
            logger.info("Request to execute local job convert step");
        }

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
