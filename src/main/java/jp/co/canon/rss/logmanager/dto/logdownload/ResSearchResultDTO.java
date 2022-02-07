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
public class ResSearchResultDTO {
    private String client;
    private String created;
    private String id;
    private List<ResLogFileDTO> lists;
    private String status;
    private String step;
    private String [] error;
}
