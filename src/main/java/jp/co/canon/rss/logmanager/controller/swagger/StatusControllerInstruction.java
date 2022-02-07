package jp.co.canon.rss.logmanager.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface StatusControllerInstruction {
    // remote job stop status
    @Operation(summary = "Get stop status of registered Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "stopped",
                                            description = "job is stopped",
                                            value = "{\n  \"stop\": \"true\"\n}"),
                                    @ExampleObject(
                                            name = "running",
                                            description = "job is running",
                                            value = "{\n  \"stop\": \"false\"\n}")
                            })),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    ResponseEntity<?> getStatusRemoteJob(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to start log analysis", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int jobId);
}
