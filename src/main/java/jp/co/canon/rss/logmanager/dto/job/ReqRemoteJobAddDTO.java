package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ReqRemoteJobAddDTO {
    private Integer jobId;
    private String jobName;
    private int siteId;
    private int [] planIds;

    List<ReqJobStepAddDTO> steps;
}