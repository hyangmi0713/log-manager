package jp.co.canon.rss.logmanager.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResRemoteJobStepEnableDTO {
    private int stepId;
    private String stepType;
    private String stepName;
    private Boolean enable;

    public ResRemoteJobStepEnableDTO(int stepId, String stepType, String stepName, Boolean enable) {
        this.stepId = stepId;
        this.stepType = stepType;
        this.stepName = stepName;
        this.enable = enable;
    }
}