package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResRegisteredJobDTO {
    private String companyName;
    private String fabName;
    private int jobId;
    private String jobName;
    private int siteId;
    private Boolean stop;
}
