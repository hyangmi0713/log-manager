package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ResRemoteJobDetailDTO {
    private int siteId;
    private int jobId;
    private String siteName;
    private String jobName;
    private int [] planIds;

    List<ResRemoteJobStepDetailDTO> steps;
}
