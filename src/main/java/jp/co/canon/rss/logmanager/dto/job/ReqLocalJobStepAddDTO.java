package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqLocalJobStepAddDTO {
    private Boolean enable;
    private String stepType;
    private String [] customEmails;
    private int [] emailBookIds;
    private int [] groupBookIds;
    private int [] fileIndices;
}