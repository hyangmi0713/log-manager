package jp.co.canon.rss.logmanager.dto.job;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ResRemoteJobStepDetailDTO {
    private int stepId;
    private String uuid;
    private String stepType;
    private String stepName;
    private Boolean enable;
    private String mode;
    private String [] time;
    private String cycle;
    private int period;
    private String preStep;
    private String nextStep;
    private Boolean isEmail;
    private String [] customEmails;
    private List<AddressBookDTO> emailBook;
    private List<AddressBookDTO> groupBook;
    private String subject;
    private String body;
    private int before;
    private List<ResCrasDataDTO> selectJudgeRules;
    private String description;
    private String scriptType;
    private String script;
    private int [] fileIndices;
}