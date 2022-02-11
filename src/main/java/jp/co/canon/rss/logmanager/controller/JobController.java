package jp.co.canon.rss.logmanager.controller;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.*;
import jp.co.canon.rss.logmanager.dto.site.ResPlanDTO;
import jp.co.canon.rss.logmanager.service.JobService;
import jp.co.canon.rss.logmanager.controller.swagger.JobControllerInstruction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_JOB_URL)
public class JobController implements JobControllerInstruction {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Add Remote Job
    @PostMapping(ReqURLController.API_POST_NEW_REMOTE_JOB)
    public ResponseEntity<?> addRemoteJob(HttpServletRequest request, @RequestBody ReqRemoteJobAddDTO reqRemoteJobAddDTO) {
        try {
            ResJobIdDTO addRemoteJobId = jobService.addRemoteJob(reqRemoteJobAddDTO);
            return ResponseEntity.status(HttpStatus.OK).body(addRemoteJobId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Detail Info
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_DETAIL)
    public ResponseEntity<?> getRemoteJobDetail(HttpServletRequest request, @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            ResRemoteJobDetailDTO resRemoteJobDetail = jobService.getRemoteJobDetail(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(resRemoteJobDetail);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Step Info
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_STEP)
    public ResponseEntity<?> getRemoteJobStep(
            HttpServletRequest request,
            @Valid @PathVariable(value = "jobId") @NotNull int jobId, @Valid @PathVariable(value = "stepId") @NotNull int stepId) {
        try {
            ResRemoteJobStepDetailDTO resRemoteJobStep = jobService.getRemoteJobStep(stepId);
            return ResponseEntity.status(HttpStatus.OK).body(resRemoteJobStep);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Enable Step
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_STEP_ENABLE)
    public ResponseEntity<?> getRemoteJobStepEnable(
            HttpServletRequest request, @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            List<ResRemoteJobStepEnableDTO> resRemoteJobStepEnable = jobService.getRemoteJobStepEnable(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(resRemoteJobStepEnable);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete Remote Job Step
    @DeleteMapping(ReqURLController.API_DELETE_REMOTE_JOB_STEP)
    public ResponseEntity<?> deleteRemoteJobStep(
            HttpServletRequest request, @Valid @PathVariable(value = "jobId") @NotNull int jobId, @Valid @PathVariable(value = "stepId") @NotNull int stepId) {
        try {
            jobService.deleteRemoteJobStep(stepId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete Remote/local Job
    @DeleteMapping(ReqURLController.API_DELETE_JOB)
    public ResponseEntity<?> deleteRemoteJob(
            HttpServletRequest request,
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @Valid @PathVariable(value = "jobType") @NotNull String jobType) {
        try {
            jobService.deleteJob(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Edit Remote Job
    @PutMapping(ReqURLController.API_PUT_REMOTE_JOB)
    public ResponseEntity<?> updateJob(
            HttpServletRequest request,
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @RequestBody ReqRemoteJobAddDTO reqRemoteJobAddDTO) {
        try {
            ResJobIdDTO editRemoteJobId = jobService.editRemoteJob(jobId, reqRemoteJobAddDTO);
            return ResponseEntity.status(HttpStatus.OK).body(editRemoteJobId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // start remote job(stop:false)
    @PatchMapping(ReqURLController.API_PATCH_REMOTE_JOB_RUN)
    public ResponseEntity<?> runRemoteJob(
            HttpServletRequest request, @Valid @PathVariable(value = "id") @NotNull int jobId) {
        try {
            ResJobIdDTO resJobIdDTO = jobService.runStopRemoteJob(jobId, "run");
            return ResponseEntity.status(HttpStatus.OK).body(resJobIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // stop remote job(stop:true)
    @PatchMapping(ReqURLController.API_PATCH_REMOTE_JOB_STOP)
    public ResponseEntity<?> stopRemoteJob(
            HttpServletRequest request, @Valid @PathVariable(value = "id") @NotNull int jobId) {
        try {
            ResJobIdDTO resJobIdDTO = jobService.runStopRemoteJob(jobId, "stop");
            return ResponseEntity.status(HttpStatus.OK).body(resJobIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add Local Job
    @PostMapping(ReqURLController.API_POST_NEW_LOCAL_JOB)
    public ResponseEntity<?> setLocalJob(HttpServletRequest request, @RequestBody ReqLocalJobAddDTO reqLocalJobAddDTO) {
        try {
            ResJobIdDTO resJobIdDTO = jobService.addLocalJob(reqLocalJobAddDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resJobIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Plan List
    @GetMapping(ReqURLController.API_GET_PLAN_LIST)
    public ResponseEntity<?> getPlanInfo(HttpServletRequest request, @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            List<ResPlanDTO> resPlanDTOList = jobService.getPlanList(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resPlanDTOList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // start manual job
    @PatchMapping(ReqURLController.API_GET_MANUAL)
    public ResponseEntity<?> getManualExcute(HttpServletRequest request,
                                          @Valid @PathVariable(value = "jobId") @NotNull int jobId,
                                          @Valid @PathVariable(value = "stepId") @NotNull int stepId) throws Exception {
        try {
            jobService.runManualExcute(jobId, stepId, true);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
