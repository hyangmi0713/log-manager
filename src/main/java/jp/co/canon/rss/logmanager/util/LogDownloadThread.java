package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.logdownload.ResCheckFileDownloadCrasDTO;
import jp.co.canon.rss.logmanager.scheduler.Scheduler;
import jp.co.canon.rss.logmanager.scheduler.SchedulerStrategy;
import jp.co.canon.rss.logmanager.vo.LogDownloadStatusVo;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class LogDownloadThread implements Runnable {
    private ResCheckFileDownloadCrasDTO resCheckFileDownloadCrasDTO;
    private LogDownloadStatusVo logDownloadStatusVo;
    private HttpEntity reqHeaders;
    private String logDownloadPath;
    public LogDownloadThread(ResCheckFileDownloadCrasDTO resCheckFileDownloadCrasDTO,
                             LogDownloadStatusVo logDownloadStatusVo, HttpEntity reqHeaders, String logDownloadPath) {
        this.resCheckFileDownloadCrasDTO = resCheckFileDownloadCrasDTO;
        this.logDownloadStatusVo = logDownloadStatusVo;
        this.reqHeaders = reqHeaders;
        this.logDownloadPath = logDownloadPath;
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
        }
        log.info("==================================================================================================");
    }
}
