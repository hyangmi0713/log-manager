package jp.co.canon.rss.logmanager.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.controller.examples.JobExamples;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.model.job.ReqLocalJob;
import jp.co.canon.rss.logmanager.controller.model.job.ReqRemoteJob;
import jp.co.canon.rss.logmanager.controller.model.job.ResRemoteJobDetail;
import jp.co.canon.rss.logmanager.controller.model.site.PlansDTO;
import jp.co.canon.rss.logmanager.dto.job.ReqLocalJobAddDTO;
import jp.co.canon.rss.logmanager.dto.job.ReqRemoteJobAddDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface JobControllerInstruction {
    // Add Remote Job
    @Operation(summary = "Add new Remote Job")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Remote Job added successfully)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> addRemoteJob(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Remote job data to add the information",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReqRemoteJob.class),
                            examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
            )
            @RequestBody ReqRemoteJobAddDTO reqRemoteJobAddDTO);

    // GET Remote Job Detail Info
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> getRemoteJobDetail(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId);

    // GET Remote Job Step Info
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> getRemoteJobStep(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @Parameter(name = "stepId", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "stepId") @NotNull int stepId);

    // GET Remote Job Enable Step
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> getRemoteJobStepEnable(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId);

    // Delete Remote Job Step
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(summary = "Delete specified Remote Job (Delete related Notification information and MailContext information at the same time)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> deleteRemoteJobStep(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @Parameter(name = "stepId", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "stepId") @NotNull int stepId);

    // Delete Remote/local Job
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(summary = "Delete specified Remote Job (Delete related Notification information and MailContext information at the same time)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> deleteRemoteJob(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @Parameter(name = "stepType", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "stepType") @NotNull String stepType);

    // Edit Remote Job
    @Operation(summary = "Modify the information of the already registered Remote Job")
    @Parameters({
            @Parameter(name = "id", description = "Job ID to modify the information", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job information modify)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> updateJob(
            HttpServletRequest request,
            @Parameter(name = "jobId", description = "Job ID to modify the information", required = true, example = "3")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Remote job modify to add the information",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReqRemoteJob.class),
                            examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
            )
            @Parameter(
                    name = "jsonObj",
                    description = "Remote job data to modify the information",
                    required = true,
                    schema = @Schema(implementation = ReqRemoteJob.class),
                    example = JobExamples.REQ_ADD_EDIT_REMOTE_JOB
            )
            @RequestBody ReqRemoteJobAddDTO reqRemoteJobAddDTO);

    // start remote job(stop:false)
    @Operation(summary = "Start log analysis of already registered Remote Job")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful start of log analysis)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> runRemoteJob(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to start log analysis", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int jobId);

    // stop remote job(stop:true)
    @Operation(summary = "Stop log analysis of already registered Remote Job")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful log analysis cancellation)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> stopRemoteJob(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to stop log analysis", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int jobId);

    // Add Local Job
    @Operation(summary = "New Local Job registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful registration of new Local Job)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> setLocalJob(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Local job data to add the information",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReqLocalJob.class),
                            examples = @ExampleObject(value = "{\n  \"siteId\": 10,\n  \"fileIndices\": [\n    64,\n    65\n  ]\n}"))
            )
            @RequestBody ReqLocalJobAddDTO reqLocalJobAddDTO);

    // GET Plan List
    @Operation(summary="Get plan list from Rapid Collector for specified site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successfully retrieved the plan list for the specified Site)",
                    content = { @Content(
                            schema = @Schema(implementation = PlansDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_PLAN_LIST_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    ResponseEntity<?> getPlanInfo(HttpServletRequest request,
                                  @Parameter(
                                          schema = @Schema(example = "1"),
                                          description = "The Site ID of the Site for which you want to get the plan", required = true)
                                  @Valid @PathVariable(value = "id") @NotNull int siteId);

    // start manual job
    @Operation(summary="Get plan list from Rapid Collector for specified site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful start of log analysis)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> getManualExcute(HttpServletRequest request,
                                      @Parameter(
                                              schema = @Schema(example = "1"),
                                              description = "The Site ID of the Site for which you want to get the plan", required = true)
                                      @Valid @PathVariable(value = "jobId") @NotNull int jobId,
                                      @Parameter(
                                              schema = @Schema(example = "1"),
                                              description = "The Site ID of the Site for which you want to get the plan", required = true)
                                      @Valid @PathVariable(value = "stepId") @NotNull int stepId) throws Exception;

//    // get time line
//    @Operation(summary="Get plan list from Rapid Collector for specified site")
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode="200",
//                    description="OK(Successfully retrieved the plan list for the specified Site)",
//                    content = { @Content(
//                            schema = @Schema(implementation = PlansDTO.class),
//                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            examples = @ExampleObject(
//                                    name = "example1",
//                                    value = SiteExamples.GET_PLAN_LIST_RES)) }
//            ),
//            @ApiResponse(responseCode="400", description="Bad Request"),
//            @ApiResponse(responseCode="404", description="Not Found"),
//            @ApiResponse(responseCode="500", description="Internal Server Error")
//    })
//    ResponseEntity<?> getTimeLine(HttpServletRequest request,
//                                  @Parameter(
//                                          schema = @Schema(example = "1"),
//                                          description = "The Site ID of the Site for which you want to get the plan", required = true)
//                                  @Valid @PathVariable(value = "id") @NotNull int id) throws Exception;
}
