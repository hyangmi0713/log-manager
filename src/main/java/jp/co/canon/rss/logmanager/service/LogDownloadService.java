package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.logdownload.*;
import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.repository.LogDownloadStatusRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.util.LogDownloadThread;
import jp.co.canon.rss.logmanager.vo.LogDownloadStatusVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service()
public class LogDownloadService {
    @Value("${log-download.max-period}")
    private String maxPeriod;
    @Value("${log-download.files}")
    private String maxFiles;
    @Value("${log-download.log-download-dir}")
    private String logDownloadPath;

    SiteRepository siteRepository;
    LogDownloadStatusRepository logDownloadStatusRepository;
    AnalysisToolService analysisToolService;

    public LogDownloadService(SiteRepository siteRepository, LogDownloadStatusRepository logDownloadStatusRepository,
                              AnalysisToolService analysisToolService) {
        this.siteRepository = siteRepository;
        this.logDownloadStatusRepository = logDownloadStatusRepository;
        this.analysisToolService = analysisToolService;
    }

    @PostConstruct
    private void _init() {
        List<LogDownloadStatusVo> logDownloadStatusVoList = logDownloadStatusRepository.findAll();

        for(LogDownloadStatusVo logDownloadStatusVo : logDownloadStatusVoList) {
            if(logDownloadStatusVo.getStatus()!=null) {
                if(logDownloadStatusVo.getStatus().equals(ReqURLController.DOWNLOAD_STATUS_PROCESSING)) {
                    logDownloadStatusVo.setStatus(ReqURLController.DOWNLOAD_STATUS_FAILURE);
                    logDownloadStatusVo.setError(new String[] {"Forcibly terminated when starting the system"});
                    logDownloadStatusRepository.save(logDownloadStatusVo);
                }
            }
        }
        log.info("The running task was forcibly terminated.");
    }

    public ResDownloadInfoDTO getLogDownloadList() throws Exception {
        ResDownloadInfoDTO resDownloadInfoDTO = new ResDownloadInfoDTO();
        Map<String, Map<String, List<String>>> lists = new HashMap<>();

        List<SiteVo> siteVoList = siteRepository.findAll();

        for(SiteVo siteVo :siteVoList) {
            CallRestAPI callRestAPI = new CallRestAPI();

            Map<String, List<String>> info = new HashMap<>();

            List<String> machineName = new ArrayList<>();
            String machineNameAddr = String.format(ReqURLController.API_GET_ALL_MPA_LIST,
                    siteVo.getCrasAddress(),
                    siteVo.getCrasPort(),
                    siteVo.getCrasFabName());
            ResponseEntity<?> machineNameResponse = callRestAPI.getRestAPI(machineNameAddr, ResMachineListDTO[].class);
            List<ResMachineListDTO> resMachineListDTOList = Arrays.asList((ResMachineListDTO[]) machineNameResponse.getBody());
            for(ResMachineListDTO resMachineListDTO : resMachineListDTOList)
                machineName.add(resMachineListDTO.getEquipment_name());

            List<String> categoryName = new ArrayList<>();
            String categoryNameAddr = String.format(ReqURLController.API_GET_ALL_CATEGORY_LIST,
                    siteVo.getCrasAddress(),
                    siteVo.getCrasPort());
            ResponseEntity<?> categoryNameResponse = callRestAPI.getRestAPI(categoryNameAddr, ResCategoryListDTO[].class);
            List<ResCategoryListDTO> resCategoryListDTOList = Arrays.asList((ResCategoryListDTO[]) categoryNameResponse.getBody());
            for(ResCategoryListDTO resCategoryListDTO : resCategoryListDTOList)
                categoryName.add(resCategoryListDTO.getCategoryName());

            List<String> requestDownloadId = new ArrayList<>();
            List<LogDownloadStatusVo> logDownloadStatusVoList = logDownloadStatusRepository.findBySiteId(siteVo.getSiteId());
            for(LogDownloadStatusVo logDownloadStatusVo : logDownloadStatusVoList) {
                if(logDownloadStatusVo.getSiteId() == siteVo.getSiteId())
                    if(logDownloadStatusVo.getRidCrasDownload() != null)
                        requestDownloadId.add(logDownloadStatusVo.getClientId());
            }

            info.put("machineName", machineName);
            info.put("categoryName", categoryName);
            info.put("requestId", requestDownloadId);

            lists.put(siteVo.getCrasCompanyName()+"_"+siteVo.getCrasFabName(), info);
        }
        resDownloadInfoDTO.setLists(lists);

        return resDownloadInfoDTO;
    }

    public ResClientIdDTO reqLogFileSearch(ReqLogFileSearchDTO reqLogFileSearchDTO) throws Exception {
        if (reqLogFileSearchDTO.getFtp_type().equals(ReqURLController.DOWNLOAD_FTP_CRAS)
                || reqLogFileSearchDTO.getFtp_type().equals(ReqURLController.DOWNLOAD_VFTP_SSS_CRAS)) {
            Optional<ResSitesDetailDTO> siteVo = siteRepository.findByCrasCompanyNameIgnoreCaseAndCrasFabNameIgnoreCase(
                    reqLogFileSearchDTO.getUser(), reqLogFileSearchDTO.getFab());

            if (siteVo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested \"user\" or \"fab\" are not correct.");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDate startDate = LocalDate.parse(reqLogFileSearchDTO.getStart_date(), formatter);
            LocalDate endDate = LocalDate.parse(reqLogFileSearchDTO.getStart_date(), formatter);

            if (ChronoUnit.DAYS.between(startDate, endDate) > Integer.parseInt(maxPeriod))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The search period exceeded the maximum search period(" + maxPeriod + ").");

            LogDownloadStatusVo logDownloadStatusVo = new LogDownloadStatusVo()
                    .setSiteId(siteVo.get().getSiteId())
                    .setSiteVoList(siteRepository.findBySiteId(siteVo.get().getSiteId()));

            LogDownloadStatusVo initialSave = logDownloadStatusRepository.save(logDownloadStatusVo);

            ReqLogFileSearchCrasDTO reqLogFileSearchCrasDTO = new ReqLogFileSearchCrasDTO()
                    .setStart_date(reqLogFileSearchDTO.getStart_date().replaceAll("[- :]", ""))
                    .setEnd_date(reqLogFileSearchDTO.getEnd_date().replaceAll("[- :]", ""))
                    .setMachine(reqLogFileSearchDTO.getMachine())
                    .setFtp_type(reqLogFileSearchDTO.getFtp_type())
                    .setCommand(reqLogFileSearchDTO.getCategoryName());

            CallRestAPI callRestAPI = new CallRestAPI();

            String clientId = reqLogFileSearchDTO.getUser() + "_" + reqLogFileSearchDTO.getFab() + "_" + String.format("%06d", initialSave.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
            headers.set(ReqURLController.JOB_CLIENT_ID, clientId);
            log.info("client-id : " + clientId);

            HttpEntity<Object> reqGetRid = new HttpEntity(reqLogFileSearchCrasDTO, headers);
            ResponseEntity<?> responseGetRid = callRestAPI.exchange(
                    String.format(ReqURLController.API_POST_REQ_RID, siteVo.get().getCrasAddress(), siteVo.get().getCrasPort()),
                    HttpMethod.POST,
                    reqGetRid,
                    ReqGetRidDTO.class);

            ReqGetRidDTO reqGetRidDTO = (ReqGetRidDTO) responseGetRid.getBody();
            logDownloadStatusRepository.updateRidCrasSearch(initialSave.getId(), reqGetRidDTO.getRid());
            logDownloadStatusRepository.updateClientId(initialSave.getId(), clientId);

            return new ResClientIdDTO().setClientId(clientId);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\"ftp_type\" only supports \"ftp\", \"vftp_sss\".");
    }

    public ResLogFileSearchDTO checkLogFileSearch(String clientId) throws Exception {
        LogDownloadStatusVo logDownloadStatusVo = logDownloadStatusRepository.findByClientId(clientId);

        if(logDownloadStatusVo==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested \"requestId\" is not correct.");

        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, clientId);
        log.info("client-id : " + clientId);

        HttpEntity<Object> reqGetSearchResult = new HttpEntity("", headers);

        CallRestAPI callRestAPI = new CallRestAPI();
        ResponseEntity<?> responseSearchResult = callRestAPI.exchange(
                String.format(ReqURLController.API_GET_REQ_SEARCH_RESULT,
                        logDownloadStatusVo.getSiteVoList().getCrasAddress(), logDownloadStatusVo.getSiteVoList().getCrasPort(),
                        logDownloadStatusVo.getRidCrasSearch()),
                HttpMethod.GET,
                reqGetSearchResult,
                ResSearchResultDTO.class);
        ResSearchResultDTO resSearchResultDTO = (ResSearchResultDTO) responseSearchResult.getBody();

        ResLogFileSearchDTO resLogFileSearchDTO = new ResLogFileSearchDTO()
                .setRequestId(clientId)
                .setStatus(resSearchResultDTO.getStatus())
                .setLists(resSearchResultDTO.getLists())
                .setError(resSearchResultDTO.getError());

        return resLogFileSearchDTO;
    }

    public ResponseEntity<?> getLogFileDownload(ReqLogFileDownloadDTO reqLogFileDownloadDTO) throws Exception {
        LogDownloadStatusVo logDownloadStatusVo = logDownloadStatusRepository.findByClientId(reqLogFileDownloadDTO.getRequestId());

        if(logDownloadStatusVo==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested \"requestId\" is not correct.");

        if(reqLogFileDownloadDTO.getFtp_type().equals(ReqURLController.DOWNLOAD_FTP_CRAS)
                || reqLogFileDownloadDTO.getFtp_type().equals(ReqURLController.DOWNLOAD_VFTP_COMPAT_CRAS)
                || reqLogFileDownloadDTO.getFtp_type().equals(ReqURLController.DOWNLOAD_VFTP_SSS_CRAS)) {
            if(reqLogFileDownloadDTO.getLists().size() > Integer.parseInt(maxFiles))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The number of download files exceeded the maximum number of downloadable files(" + maxFiles +").");

            HttpHeaders headers = new HttpHeaders();
            headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
            headers.set(ReqURLController.JOB_CLIENT_ID, logDownloadStatusVo.getClientId());
            log.info("client-id : " + logDownloadStatusVo.getClientId());

            ReqLogFileDownloadCrasDTO reqLogFileDownloadCrasDTO = new ReqLogFileDownloadCrasDTO()
                    .setFtp_type(reqLogFileDownloadDTO.getFtp_type())
                    .setLists(reqLogFileDownloadDTO.getLists())
                    .setCommand(reqLogFileDownloadDTO.getCommand());

            HttpEntity<Object> reqFileDownload = new HttpEntity(reqLogFileDownloadCrasDTO, headers);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> responseGetRid = callRestAPI.exchange(
                    String.format(ReqURLController.API_POST_REQ_FILE_DOWNLOAD,
                            logDownloadStatusVo.getSiteVoList().getCrasAddress(), logDownloadStatusVo.getSiteVoList().getCrasPort()),
                    HttpMethod.POST,
                    reqFileDownload,
                    ReqGetRidDTO.class);

            ReqGetRidDTO reqGetRidDTO = (ReqGetRidDTO) responseGetRid.getBody();
            logDownloadStatusRepository.updateRidCrasDownload(logDownloadStatusVo.getId(), reqGetRidDTO.getRid());

            log.info("rid_cras_download : " + reqGetRidDTO.getRid());

            return responseGetRid;
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "\"ftp_type\" only supports \"ftp\", \"vftp_sss\", \"vftp_compat\".");
    }

    public ResCheckFileDownloadDTO getCheckFileDownload(String requestId) throws Exception {
        LogDownloadStatusVo logDownloadStatusVo = logDownloadStatusRepository.findByClientId(requestId);

        if(logDownloadStatusVo==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested \"requestId\" is not correct.");

        HttpHeaders headers = new HttpHeaders();
        headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
        headers.set(ReqURLController.JOB_CLIENT_ID, logDownloadStatusVo.getClientId());
        log.info("client-id : " + logDownloadStatusVo.getClientId());
        HttpEntity reqHeaders = new HttpEntity<>(headers);

        HttpEntity<Object> reqCheckFileDownload = new HttpEntity("", headers);

        ResCheckFileDownloadDTO resCheckFileDownloadDTO = new ResCheckFileDownloadDTO()
                .setRequestId(logDownloadStatusVo.getRidCrasDownload());

        CallRestAPI callRestAPI = new CallRestAPI();
        ResponseEntity<?> resFileDownload = callRestAPI.exchange(
                String.format(ReqURLController.API_GET_CHECK_FILE_DOWNLOAD,
                        logDownloadStatusVo.getSiteVoList().getCrasAddress(), logDownloadStatusVo.getSiteVoList().getCrasPort(),
                        logDownloadStatusVo.getRidCrasDownload()),
                HttpMethod.GET,
                reqCheckFileDownload,
                ResCheckFileDownloadCrasDTO.class);

        ResCheckFileDownloadCrasDTO resCheckFileDownloadCrasDTO = (ResCheckFileDownloadCrasDTO) resFileDownload.getBody();

        if(resCheckFileDownloadCrasDTO.getStatus().equals(ReqURLController.DOWNLOAD_STATUS_SUCCESS)) {
            LogDownloadThread logDownloadThread = new LogDownloadThread(resCheckFileDownloadCrasDTO, logDownloadStatusVo, reqHeaders, logDownloadPath);
            Thread thread = new Thread(logDownloadThread, "logDownloadThread-");
            thread.start();

            Thread.State threadState = thread.getState();

            if(threadState == Thread.State.TERMINATED) {
                List<String> downloadUrlList = new ArrayList<>();
                for(String downloadPath : resCheckFileDownloadCrasDTO.getDownload_url()) {
                    String fileName = downloadPath.split(File.separator)[downloadPath.split(File.separator).length - 1];
                    downloadUrlList.add(logDownloadPath + File.separator + fileName);
                }
                resCheckFileDownloadDTO.setStatus(ReqURLController.DOWNLOAD_STATUS_SUCCESS)
                        .setDownload_url(downloadUrlList.toArray(new String[downloadUrlList.size()]))
                        .setError(new String[0]);
            }
            else {
                resCheckFileDownloadDTO.setStatus(ReqURLController.DOWNLOAD_STATUS_PROCESSING)
                        .setDownload_url(new String[0])
                        .setError(new String[0]);
            }
        }
        else if (resCheckFileDownloadCrasDTO.getStatus().equals(ReqURLController.DOWNLOAD_STATUS_ERROR)) {
            resCheckFileDownloadDTO.setStatus(ReqURLController.DOWNLOAD_STATUS_ERROR)
                    .setDownload_url(new String[0])
                    .setError(resCheckFileDownloadCrasDTO.getError());
        }
        else if (resCheckFileDownloadCrasDTO.getStatus().equals(ReqURLController.DOWNLOAD_STATUS_RUNNING)) {
            resCheckFileDownloadDTO.setStatus(ReqURLController.DOWNLOAD_STATUS_PROCESSING)
                    .setDownload_url(new String[0])
                    .setError(new String[0]);
        }

        logDownloadStatusRepository.save(logDownloadStatusVo.setStatus(resCheckFileDownloadDTO.getStatus())
                .setDownloadURL(resCheckFileDownloadDTO.getDownload_url())
                .setError(resCheckFileDownloadDTO.getError()));

        return resCheckFileDownloadDTO;
    }
}
