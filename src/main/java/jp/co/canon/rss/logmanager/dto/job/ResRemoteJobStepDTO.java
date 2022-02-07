package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResRemoteJobStepDTO {
    private Integer jobId;
    private int stepId;
    private String uuid;
    private String status;
    private String [] error;
    private String stepName;
    private boolean enable;
    private String description;
    private String stepType;
    private String mode;
    private String [] time;
    private String cycle;
    private int period;
    private String preStep;
    private String nextStep;
    private ResLastStepDTO lastSuccess;
    private ResLastStepDTO lastFailure;
}
