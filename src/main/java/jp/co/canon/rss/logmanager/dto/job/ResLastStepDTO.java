package jp.co.canon.rss.logmanager.dto.job;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResLastStepDTO {
    private int stepId;
    @Schema(description = "collect, convert, summary, cras, version, purge, custom, notice")
    private String stepType;
    private String historyId;
    private String stepName;
    private String date;
    private Boolean manual;
    private String status;
    private String [] error;
}
