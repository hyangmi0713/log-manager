package jp.co.canon.rss.logmanager.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.controller.examples.JobExamples;
import jp.co.canon.rss.logmanager.controller.model.job.ReqRemoteJob;
import jp.co.canon.rss.logmanager.controller.model.job.ResRemoteJobDetail;
import jp.co.canon.rss.logmanager.dto.logdownload.ReqLogFileDownloadDTO;
import jp.co.canon.rss.logmanager.dto.logdownload.ReqLogFileSearchDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface LogDownloadControllerInstruction {
    // GET Log Download Information
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
    ResponseEntity<?> getErrorLogList(HttpServletRequest request);

    // REQ LOG FILE SEARCH
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
    ResponseEntity<?> reqLogFileSearch(HttpServletResponse response,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                 description = "Remote job data to add the information",
                                                 required = true,
                                                 content = @Content(
                                                         schema = @Schema(implementation = ReqRemoteJob.class),
                                                         examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
                                         )
                                         @RequestBody ReqLogFileSearchDTO reqLogFileSearchDTO);

    // CHECK LOG FILE SEARCH
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
    ResponseEntity<?> checkLogFileSearch(HttpServletResponse response,
                                         @Valid @PathVariable(value = "clientId") @com.sun.istack.NotNull String clientId);

    // Post LOG FILE SEARCH
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
    ResponseEntity<?> reqLogFileDownload(HttpServletResponse response,
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               description = "Remote job data to add the information",
                                               required = true,
                                               content = @Content(
                                                       schema = @Schema(implementation = ReqRemoteJob.class),
                                                       examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
                                       )
                                       @RequestBody ReqLogFileDownloadDTO reqLogFileDownloadDTO);

    // CHECK LOG FILE DOWNLOAD(Rapid Collector)
    @Operation(summary="Get plan list from Rapid Collector for specified site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful start of log analysis)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<?> checkFileDownload(HttpServletRequest request,
                                      @Parameter(
                                              schema = @Schema(example = "1"),
                                              description = "The Site ID of the Site for which you want to get the plan", required = true)
                                      @Valid @PathVariable(value = "requestId") @NotNull String requestId);
}
