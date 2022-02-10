package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ReqLogFileDownloadCrasDTO {
    private String step;
    private String ftp_type;
    private List<ResLogFileDTO> lists;
    private String command;
}
