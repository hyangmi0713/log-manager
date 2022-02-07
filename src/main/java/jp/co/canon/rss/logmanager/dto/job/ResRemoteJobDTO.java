package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResRemoteJobDTO {
    private Integer jobId;
    private int siteId;
    private boolean stop;
    private String companyName;
    private String fabName;
    private String jobName;
    private ResLastStepDTO lastSuccess;
    private ResLastStepDTO lastFailure;
}
