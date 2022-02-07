package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.JobExamples;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.swagger.StatusControllerInstruction;
import jp.co.canon.rss.logmanager.dto.job.*;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataSiteInfoDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.service.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_STATUS_URL)
public class StatusController implements StatusControllerInstruction {
    private StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    // GET Remote Job List
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_LIST)
    @Operation(summary = "Get status list for all Remote Jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful status list acquisition for all Remote Jobs)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_LIST))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getRemoteJobList(HttpServletRequest request) {
        try {
            List<ResRemoteJobDTO> remoteJobs = statusService.getRemoteJobs();
            return ResponseEntity.status(HttpStatus.OK).body(remoteJobs);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Step Status Info
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_STEP_LIST)
    @Operation(summary = "Get status list for all Remote Jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful status list acquisition for all Remote Jobs)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_LIST))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getRemoteJobStepList(HttpServletRequest request,
                                                  @Parameter(name = "id", description = "Job ID to delete", required = true, example = "1")
                                                  @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            List<ResRemoteJobStepDTO> remoteJobSteps = statusService.getRemoteJobSteps(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(remoteJobSteps);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Build Queue Info
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_BUILD_QUEUE)
    @Operation(summary = "Get status list for all Remote Jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful status list acquisition for all Remote Jobs)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_LIST))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getRemoteJobBuildQueue(HttpServletRequest request,
                                                  @Parameter(name = "id", description = "Job ID to delete", required = true, example = "1")
                                                  @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            List<ResLastStepDTO> remoteJobBuildQueue = statusService.getRemoteJobBuildQueue(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(remoteJobBuildQueue);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Remote Job Build Executor Status Info
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_BUILD_EXECUTOR)
    @Operation(summary = "Get status list for all Remote Jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful status list acquisition for all Remote Jobs)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_LIST))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getRemoteJobBuildExecutor(HttpServletRequest request,
                                                    @Parameter(name = "id", description = "Job ID to delete", required = true, example = "1")
                                                    @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            List<ResLastStepDTO> remoteJobBuildExecutor = statusService.getRemoteJobBuildExecutor(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(remoteJobBuildExecutor);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET Local Job List
    @GetMapping(ReqURLController.API_GET_LOCAL_JOB_LIST)
    @Operation(summary = "Get status list for all Local Jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of status list for all Local Jobs)",
                    content = @Content(
                            schema = @Schema(implementation = ResLocalJobListDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_LOCAL_JOB_LIST))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getLocalJobList(HttpServletRequest request) {
        try {
            List<ResLocalJobListDTO> resultLocalList = statusService.localJobListDTOS();
            return ResponseEntity.status(HttpStatus.OK).body(resultLocalList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site name 리스트 취득 전체
    @GetMapping(ReqURLController.API_GET_SITE_NAME_ALL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSitesNamesListAll(HttpServletRequest request) {
        try {
            List<ResSitesNamesDTO> resPlanDTOList = statusService.getSitesNamesList(false);
            return ResponseEntity.status(HttpStatus.OK).body(resPlanDTOList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site name 리스트 취득 등록 안된 것만
    @GetMapping(ReqURLController.API_GET_SITE_NAME_NOTADDED)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSitesNamesList(HttpServletRequest request) {
        try {
            List<ResSitesNamesDTO> resPlanDTOList = statusService.getSitesNamesList(true);
            return ResponseEntity.status(HttpStatus.OK).body(resPlanDTOList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사이트 정보 리스트 취득
    @GetMapping(ReqURLController.API_GET_CRAS_DATA_SITE_INFO)
    @Operation(summary="Get a list of sites that can be registered")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of a list of sites that can be registered)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_NAME_REQ)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getCrasDataSiteInfo(HttpServletRequest request) {
        try {
            List<ResCrasDataSiteInfoDTO> resCrasDataSiteInfoDTO = statusService.getCrasDataSiteInfo();
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataSiteInfoDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // remote job stop status
    @GetMapping(ReqURLController.API_GET_REMOTE_JOB_STATUS)
    public ResponseEntity<?> getStatusRemoteJob(
            HttpServletRequest request,
            @Valid @PathVariable(value = "jobId") @NotNull int jobId) {
        try {
            ResRemoteJobStatusDTO resRemoteJobStatusDTO = statusService.getStatusRemoteJob(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(resRemoteJobStatusDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET RegisteredJob
    @GetMapping(ReqURLController.API_GET_REGISTERED_JOB)
    public ResponseEntity<?> getRegisteredJob(
            HttpServletRequest request,
            @Valid @PathVariable(value = "siteId") @NotNull int siteId) {
        try {
            List<ResRegisteredJobDTO> resRegisteredJobDTO = statusService.getRegisteredJob(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resRegisteredJobDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
