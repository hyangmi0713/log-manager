package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ReqLogFileSearchDTO {
    private String user;
    private String fab;
    private String [] machine;
    private String ftp_type;
    private String [] categoryName;
    private String start_date;
    private String end_date;
}
