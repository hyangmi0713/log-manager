package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResCheckFileDownloadCrasDTO {
    private String client;
    private String created;
    private String [] download_url;
    private String [] error;
    private String id;
    private String status;
    private String step;
}
