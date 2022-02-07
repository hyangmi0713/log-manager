package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ReqLocalJobAddDTO {
    private Integer jobId;
    private int siteId;

    List<ReqJobStepAddDTO> steps;
}
