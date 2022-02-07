package jp.co.canon.rss.logmanager.scheduler;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ResMakeLogger;
import jp.co.canon.rss.logmanager.dto.job.ResStepRunDTO;
import jp.co.canon.rss.logmanager.dto.job.ResStepStatusDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.repository.*;
import jp.co.canon.rss.logmanager.service.JobService;
import jp.co.canon.rss.logmanager.util.*;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class RunStepFlow {
    @Value("${file.download-dir}")
    private String downloadPath;
    @Value("${logmonitor.logging.root}")
    private String loggingPath;

    @Autowired
    private HistoryEntityRepository historyEntityRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    AddressBookRepository addressRepository;
    @Autowired
    GroupBookRepository groupRepository;
    @Autowired
    JobService jobService;
    @Autowired
    RandomString randomString;

    // job stop when system is started
    @PostConstruct
    private void _init() {
        List<HistoryEntity> historyEntityListEndDate =  historyEntityRepository.findByEndDate(null);
        for(HistoryEntity historyEntityEndDate : historyEntityListEndDate) {
            historyEntityRepository.updateHistoryEndingTime(
                    historyEntityEndDate.getHistoryId(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            historyEntityRepository.updateHistoryStatus(
                    historyEntityEndDate.getHistoryId(), RunStep.STATUS_FAILURE);
        }

        List<HistoryEntity> historyEntityListStatus =
                historyEntityRepository.findByStatusOrStatus(RunStep.STATUS_PROCESSING, RunStep.STATUS_NOTBUILD);
        for(HistoryEntity historyEntityStatus : historyEntityListStatus)
            historyEntityRepository.updateHistoryStatus(
                    historyEntityStatus.getHistoryId(), RunStep.STATUS_FAILURE);
    }

    // Make Logger
    public ResMakeLogger makeLogger(StepEntity stepEntity) {
        RandomString randomString = new RandomString();
        String filename = stepEntity.getStepType() + "_" + randomString.numberGen(8);

        File file = Paths.get(loggingPath + filename + File.separator, filename + ".log").toFile();
        FileLog fileLog = new FileLog(file, filename);
        Logger logger = fileLog.getLogger();

        ResMakeLogger resMakeLogger = new ResMakeLogger()
                .setLogger(logger)
                .setRid(filename);

        return resMakeLogger;
    }

    // Add History Table
    public HistoryEntity addHistoryTable(StepEntity stepEntity, Logger logger, boolean manual, String rid) {
        HistoryEntity historyEntity = new HistoryEntity()
                .setJobId(stepEntity.getJobId())
                .setStepId(stepEntity.getStepId())
                .setStepType(stepEntity.getStepType())
                .setRunDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setIsManual(manual)
                .setStatus(RunStep.STATUS_PROCESSING)
                .setRid(rid)
                .setJobEntity(stepEntity.getJobEntity())
                .setStepEntity(stepEntity);

        HistoryEntity historyId = historyEntityRepository.save(historyEntity);
        logger.info("add new step on history table - rid : " + historyId.getRid());

        if(historyId == null)
            new ResponseStatusException(HttpStatus.NOT_FOUND);

        return historyId;
    }

    // Get Cras Server Address
    public String getCrasServer(StepEntity stepEntity, Logger logger) {
        Optional<JobEntity> jobEntity = jobRepository.findByJobId(stepEntity.getJobId());
        String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
                jobEntity.get().getSiteVo().getCrasAddress(), jobEntity.get().getSiteVo().getCrasPort());
        logger.info("crasServer Address : " + crasServer);

        return crasServer;
    }

    // Set Headers
    public HttpHeaders setHeaders(StepEntity stepEntity, Logger logger) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, stepEntity.getJobEntity().getType() + "_" + String.format("%06d", stepEntity.getJobId()));
        logger.info("client id : "
                + stepEntity.getJobEntity().getType() + "_" + String.format("%06d", stepEntity.getJobId()));

        return headers;
    }

    // Get ridCras
    public String getRidCras(Object response, Logger logger) {
        Gson gson = new Gson();
        ResStepRunDTO resRunCollectDTO = gson.fromJson(response.toString(), ResStepRunDTO.class);
        logger.info("get rid from cras server - Rid : " + resRunCollectDTO.getRid());

        return resRunCollectDTO.getRid();
    }

    // Get Job Status
    public ResStepStatusDTO getJobStatus(String getStepStatusURL, String ridCras, HttpHeaders headers, int historyId, Logger logger) throws ConvertException {
        logger.info("Cras Server request URL : " + getStepStatusURL);

        HttpEntity reqHeaders = new HttpEntity<>(headers);
        CallRestAPI callRestAPI = new CallRestAPI();
        ResStepStatusDTO resStepStatusDTO = new ResStepStatusDTO();

        resStepStatusDTO.setStatus(RunStep.STATUS_PROCESSING);
        String [] jobError = {};

        long startTime = System.currentTimeMillis();

        do {
            HistoryEntity historyEntity = historyEntityRepository.findByHistoryId(historyId);

            if(resStepStatusDTO.getStatus().equals(RunStep.STATUS_TIMEOUT))
                resStepStatusDTO.setStatus(RunStep.STATUS_FAILURE);
            else {
                // 상태 취득
                ResponseEntity<?> resStepStatus = callRestAPI.getWithCustomHeaderRestAPI(
                        getStepStatusURL, reqHeaders, ResStepStatusDTO.class);
                resStepStatusDTO = (ResStepStatusDTO) resStepStatus.getBody();
                jobError = resStepStatusDTO.getError();
                logger.info("get status of step from cras server : " + resStepStatusDTO.getStatus());

                switch (resStepStatusDTO.getStatus()) {
                    case (RunStep.STATUS_CRAS_ERROR):
                    case (RunStep.STATUS_CRAS_CANCEL):
                        resStepStatusDTO.setStatus(RunStep.STATUS_FAILURE);
                        break;
                    case (RunStep.STATUS_CRAS_IDLE):
                        resStepStatusDTO.setStatus(RunStep.STATUS_NOTBUILD);
                        break;
                    case (RunStep.STATUS_CRAS_RUNNING):
                        resStepStatusDTO.setStatus(RunStep.STATUS_PROCESSING);
                        break;
                }
            }

            // history 테이블 status, error 컬럼 업데이트
            historyEntity.setStatus(resStepStatusDTO.getStatus());
            historyEntity.setError(jobError);
            historyEntity.setRidCras(ridCras);
            historyEntityRepository.save(historyEntity);
            logger.info("update status and error, ridCras column of history table");

            if (resStepStatusDTO.getStatus().equals(RunStep.STATUS_SUCCESS)
                    || resStepStatusDTO.getStatus().equals(RunStep.STATUS_FAILURE)
                    || resStepStatusDTO.getStatus().equals(RunStep.STATUS_NODATA))
                return resStepStatusDTO;

            try {
                Thread.sleep(10 * 1000);
                if((System.currentTimeMillis()-startTime) > (24 * 60 * 60 * 1000)) {
                    resStepStatusDTO.setStatus(RunStep.STATUS_TIMEOUT);
                    jobError[0] = "Job ended due to timeout occurrence";
                    logger.info("Job ended due to timeout occurrence");
                }
            } catch (InterruptedException e) {
                logger.error(e.toString());
                e.printStackTrace();
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                logger.error(errors.toString());
            }
        } while (resStepStatusDTO.getStatus().equals(RunStep.STATUS_NOTBUILD)
                || resStepStatusDTO.getStatus().equals(RunStep.STATUS_PROCESSING)
                || resStepStatusDTO.getStatus().equals(RunStep.STATUS_TIMEOUT));

        return null;
    }

    // Send Report
    public Boolean sendReport(ResStepStatusDTO resStepStatusDTO, String crasServer, HttpHeaders headers, StepEntity stepEntity, Logger logger) {
        CallRestAPI callRestAPI = new CallRestAPI();
        List<String> attachFileList = new ArrayList<>();
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        String mailBody = null;
        Boolean mailResult = true;

        Optional<JobEntity> jobEntity = jobRepository.findByJobId(stepEntity.getJobId());
        SiteVo siteVo = siteRepository.findBySiteId(jobEntity.get().getSiteId());

        MailSenderSetting mailSenderSetting = new MailSenderSetting();
        JavaMailSender javaMailSender = mailSenderSetting.getMailSenderSetting(siteVo);
        MailSender mailSender = new MailSender();
        GetRecipients getRecipients = new GetRecipients(addressRepository, groupRepository);
        logger.info("Complete Mail Sender Setting");

        try {
            for (String downloadUrl : resStepStatusDTO.getDownload_url()) {
                if (downloadUrl.contains("html")) {
                    ResponseEntity<?> res = callRestAPI.getWithCustomHeaderRestAPI(
                            crasServer + downloadUrl, reqHeaders, String.class);
                    mailBody = (String) res.getBody();
                    logger.info("Cras Server request URL : " + crasServer + downloadUrl);
                } else {
                    logger.info("Cras Server request URL : " + crasServer + downloadUrl);
                    String[] fileNameSplit = downloadUrl.split("/");
                    String fileName = LocalDateTime.now().
                            format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + fileNameSplit[fileNameSplit.length - 1];
                    DownloadStreamInfo res = callRestAPI.getWithCustomHeaderDownloadFileRestAPI(
                            crasServer + downloadUrl, reqHeaders, downloadPath, fileName);
                    attachFileList.add(downloadPath + "/" + fileName);
                }
            }

            mailResult = mailSender.sendMessageWithAttachment(javaMailSender,
                    siteVo.getEmailFrom(),
                    getRecipients.getRecipients(stepEntity),
                    stepEntity.getSubject(),
                    mailBody == null ? "" : mailBody,
                    attachFileList);
        } catch (Exception e) {
            mailResult = false;
        }
        return mailResult;
    }

    // Send Error Notice
    public void sendErrorNotice(StepEntity stepEntity, Logger logger) {
        try {
            StepEntity stepEntityNotice = stepRepository.findByJobIdAndStepType(stepEntity.getJobId(), RunStep.STEPTYPE_NOTICE);

            if (stepEntityNotice != null) {
                if (stepEntityNotice.getEnable() == true) {
                    Optional<JobEntity> jobEntity = jobRepository.findByJobId(stepEntity.getJobId());
                    SiteVo siteVo = siteRepository.findBySiteId(jobEntity.get().getSiteId());

                    MailSenderSetting mailSenderSetting = new MailSenderSetting();
                    JavaMailSender javaMailSender = mailSenderSetting.getMailSenderSetting(siteVo);
                    MailSender mailSender = new MailSender();
                    GetRecipients getRecipients = new GetRecipients(addressRepository, groupRepository);
                    logger.info("Complete Mail Sender Setting");

                    //TODO: body html
                    String emailTemplate = "body";

                    mailSender.sendMessageWithAttachment(javaMailSender,
                            siteVo.getEmailFrom(),
                            getRecipients.getRecipients(stepEntityNotice),
                            stepEntityNotice.getSubject(),
                            emailTemplate,
                            new ArrayList<>());

                    logger.info("Error Notice Mail Send.");
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error(errors.toString());
        }
    }

    // Update Ending Time Column of History Table
    public void updateEndingTime(int historyId, Logger logger) {
        historyEntityRepository.updateHistoryEndingTime(
                historyId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("update ending time column of history table");
    }

    // find previoud step
    public void findPreStep(StepEntity stepEntity) {
        List<StepEntity> stepEntityList = stepRepository.findByPreStep(stepEntity.getUuid());

        for(StepEntity runStep : stepEntityList) {
            if(runStep.getEnable() == true)
                jobService.runManualExcute(runStep.getJobId(), runStep.getStepId(), false);
        }
    }

    // stop step
    public void stopStep(int jobId) {
        List<HistoryEntity> historyEntityList = historyEntityRepository.findByJobId(jobId);

        for (HistoryEntity historyEntity : historyEntityList) {
            File file = Paths.get(loggingPath + historyEntity.getRid() + File.separator, historyEntity.getRid() + ".log").toFile();
            FileLog fileLog = new FileLog(file, historyEntity.getRid());
            Logger logger = fileLog.getLogger();

            if (historyEntity.getEndDate() == null
                    || historyEntity.getStatus().equals(RunStep.STATUS_NOTBUILD)
                    || historyEntity.getStatus().equals(RunStep.STATUS_PROCESSING)
                    || historyEntity.getStatus().equals(RunStep.STATUS_TIMEOUT)) {
                String[] error = {LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + " : This job was forced to end."};

                historyEntity.setStatus(RunStep.STATUS_FAILURE)
                        .setEndDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .setError(error);
                historyEntityRepository.save(historyEntity);
            }

            logger.info("All tasks are forced to end.");
        }
    }
}
