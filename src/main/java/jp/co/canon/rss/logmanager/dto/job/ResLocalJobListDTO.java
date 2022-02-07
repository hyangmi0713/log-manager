package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResLocalJobListDTO {
    private Integer jobId;
    private int siteId;
    private String companyName;
    private String fabName;
    private boolean stop;
    private String stepType;
    private String status;
    private String [] error;
    private int [] fileIndices;
    private String [] fileOriginalNames;
    private String registeredDate;
    private int stepId;
    private String historyId;
}
