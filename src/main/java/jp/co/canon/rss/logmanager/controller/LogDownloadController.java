package jp.co.canon.rss.logmanager.controller;

import com.sun.istack.NotNull;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.swagger.LogDownloadControllerInstruction;
import jp.co.canon.rss.logmanager.dto.logdownload.*;
import jp.co.canon.rss.logmanager.service.LogDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_LOG_URL)
public class LogDownloadController implements LogDownloadControllerInstruction {
    private LogDownloadService logDownloadService;

    public LogDownloadController(LogDownloadService logDownloadService) {
        this.logDownloadService = logDownloadService;
    }

    // GET Log Download Information
    @GetMapping(ReqURLController.API_GET_DOWNLOAD_INFO)
    public ResponseEntity<?> getErrorLogList(HttpServletRequest request) {
        try {
            ResDownloadInfoDTO resDownloadInfoDTO = logDownloadService.getLogDownloadList();
            return ResponseEntity.status(HttpStatus.OK).body(resDownloadInfoDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // REQ LOG FILE SEARCH
    @PostMapping(ReqURLController.API_POST_REQ_LOGFILE_SEARCH)
    public ResponseEntity<?> reqLogFileSearch(HttpServletResponse response, @RequestBody ReqLogFileSearchDTO reqLogFileSearchDTO) {
        try {
            ResClientIdDTO resClientIdDTO = logDownloadService.reqLogFileSearch(reqLogFileSearchDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resClientIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CHECK LOG FILE SEARCH
    @GetMapping(ReqURLController.API_GET_CHECK_LOGFILE_SEARCH)
    public ResponseEntity<?> checkLogFileSearch(HttpServletResponse response,
                                                @Valid @PathVariable(value = "clientId") @NotNull String clientId) {
        try {
            ResLogFileSearchDTO resLogFileSearchDTO = logDownloadService.checkLogFileSearch(clientId);
            return ResponseEntity.status(HttpStatus.OK).body(resLogFileSearchDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // REQ LOG FILE DOWNLOAD
    @PostMapping(ReqURLController.API_POST_REQ_DOWNLOAD)
    public ResponseEntity<?> reqLogFileDownload(HttpServletResponse response, @RequestBody ReqLogFileDownloadDTO reqLogFileDownloadDTO) {
        try {
            ResponseEntity<?> resLogFileDownloadDTO = logDownloadService.getLogFileDownload(reqLogFileDownloadDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resLogFileDownloadDTO.getStatusCodeValue());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CHECK LOG FILE DOWNLOAD(Rapid Collector)
    @GetMapping(ReqURLController.API_GET_CHECK_DOWNLOAD_STATUS)
    public ResponseEntity<?> checkFileDownload(HttpServletRequest request,
                                               @Valid @PathVariable(value = "clientId") @NotNull String clientId) {
        try {
            ResCheckFileDownloadDTO resCheckFileDownloadDTO = logDownloadService.getCheckFileDownload(clientId);
            return ResponseEntity.status(HttpStatus.OK).body(resCheckFileDownloadDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
