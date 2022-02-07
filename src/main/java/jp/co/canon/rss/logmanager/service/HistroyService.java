package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.history.ResHistoryDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.mapper.history.HistoryVoDTOMapper;
import jp.co.canon.rss.logmanager.repository.HistoryEntityRepository;
import jp.co.canon.rss.logmanager.repository.JobRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service()
public class HistroyService {
    @Value("${logmonitor.logging.root}")
    private String loggingPath;

    HistoryEntityRepository historyEntityRepository;
    JobRepository jobRepository;

    public HistroyService(HistoryEntityRepository historyEntityRepository, JobRepository jobRepository) {
        this.historyEntityRepository = historyEntityRepository;
        this.jobRepository = jobRepository;
    }

    public List<ResHistoryDTO>  getAllHistroyLogList(int jobId, int stepId) throws ConvertException {
        List<ResHistoryDTO> result = new ArrayList<>();

        List<HistoryEntity> resHistoryDTOList = historyEntityRepository.findByJobIdAndStepId(jobId, stepId);

        Collections.sort(resHistoryDTOList, new Comparator<HistoryEntity>() {
            @Override
            public  int compare(HistoryEntity o1, HistoryEntity o2) {
                return o2.getRunDate().compareTo(o1.getRunDate());
            }
        });

        for(HistoryEntity historyEntity : resHistoryDTOList) {
            if(historyEntity.getStatus().equals(RunStep.STATUS_SUCCESS)
                    || historyEntity.getStatus().equals(RunStep.STATUS_FAILURE)
                    || historyEntity.getStatus().equals(RunStep.STATUS_NODATA)) {
                ResHistoryDTO resHistoryDTO = HistoryVoDTOMapper.INSTANCE.mapResHistoryDTO(historyEntity);
                if(resHistoryDTO.getId()!=null)
                    result.add(resHistoryDTO);
            }
        }

        return result;
    }

    public String getHistroyLogListCras(String jobType, int jobId, String logId) throws ConvertException {
        JobEntity jobEntity = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String crasserver = String.format("%s:%s", jobEntity.getSiteVo().getCrasAddress(), jobEntity.getSiteVo().getCrasPort());

        HistoryEntity historyEntity = historyEntityRepository.findByRid(logId);

        String GET_BUILD_LOG_TEXT_URL = String.format(ReqURLController.API_GET_BUILD_LOG_LIST_DETAIL, crasserver, historyEntity.getRidCras());

        CallRestAPI callRestAPI = new CallRestAPI();
        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, jobType + "_" + String.format("%06d", jobId));
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        ResponseEntity<?> response = callRestAPI.getWithCustomHeaderRestAPI(GET_BUILD_LOG_TEXT_URL, reqHeaders, String [].class);
        String [] responseBodyArray = (String[]) response.getBody();
        String result = "";

        for(String responseBody : responseBodyArray) {
            result += responseBody + "\n";
        }

        return result;
    }

    public String getHistroyLogListLogMonitor(String logId) throws IOException {
        List<String> readFile = Files.readAllLines(Paths.get(loggingPath + File.separator + logId + File.separator + logId + ".log"));

        String result = "";
        for(String readFileLine : readFile)
            result += readFileLine + "\n";

        return result;
    }
}
