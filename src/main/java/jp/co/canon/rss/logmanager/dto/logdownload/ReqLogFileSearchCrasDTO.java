package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ReqLogFileSearchCrasDTO {
    private String id;
    private String step;
    private String start_date;
    private String end_date;
    private String [] machine;
    private String path;
    private String ftp_type;
    private String [] command;
}
