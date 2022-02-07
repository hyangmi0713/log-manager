package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReqJobStepAddDTO {
    private Integer stepId;
    private String uuid;
    private Boolean enable;
    private String stepType;
    private String stepName;
    private String mode;
    private String [] time;
    private String cycle;
    private int period;
    private String preStep;
    private String nextStep;
    private Boolean isEmail;
    private String [] customEmails;
    private int [] emailBookIds;
    private int [] groupBookIds;
    private String subject;
    private String body;
    private int before;
    private int [] selectJudgeRuleIds;
    private String description;
    private String scriptType;
    private String script;
    private int [] fileIndices;
}