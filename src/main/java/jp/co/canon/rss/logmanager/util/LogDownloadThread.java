package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.logdownload.ResCheckFileDownloadCrasDTO;
import jp.co.canon.rss.logmanager.repository.LogDownloadStatusRepository;
import jp.co.canon.rss.logmanager.vo.LogDownloadStatusVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;

@Slf4j
public class LogDownloadThread implements Runnable {
    private ResCheckFileDownloadCrasDTO resCheckFileDownloadCrasDTO;
    private LogDownloadStatusVo logDownloadStatusVo;
    private HttpEntity reqHeaders;
    private String logDownloadPath;
    private LogDownloadStatusRepository logDownloadStatusRepository;
    public LogDownloadThread(ResCheckFileDownloadCrasDTO resCheckFileDownloadCrasDTO,
                             LogDownloadStatusVo logDownloadStatusVo, HttpEntity reqHeaders, String logDownloadPath,
                             LogDownloadStatusRepository logDownloadStatusRepository) {
        this.resCheckFileDownloadCrasDTO = resCheckFileDownloadCrasDTO;
        this.logDownloadStatusVo = logDownloadStatusVo;
        this.reqHeaders = reqHeaders;
        this.logDownloadPath = logDownloadPath;
        this.logDownloadStatusRepository = logDownloadStatusRepository;
    }

    @SneakyThrows
    @Override
    public void run() {
        CallRestAPI callRestAPI = new CallRestAPI();

        for(String downloadPath : resCheckFileDownloadCrasDTO.getDownload_url()) {
            DownloadStreamInfo res = callRestAPI.getWithCustomHeaderDownloadFileRestAPI(
                    String.format(ReqURLController.API_GET_FILE_DOWNLOAD,
                            logDownloadStatusVo.getSiteVoList().getCrasAddress(), logDownloadStatusVo.getSiteVoList().getCrasPort(),
                            downloadPath),
                    reqHeaders, logDownloadPath, downloadPath.split("/")[downloadPath.split("/").length-1]);
            log.info("File downloading...... ID : " + logDownloadStatusVo.getId());
        }
        logDownloadStatusRepository.updateStatus(logDownloadStatusVo.getId(), ReqURLController.DOWNLOAD_STATUS_SUCCESS);
    }
}
