package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResLogFileDTO {
    private String categoryCode;
    private String categoryName;
    private String fabName;
    private String fileDate;
    private String fileName;
    private String filePath;
    private int fileSize;
    private String fileType;
    private String machineName;
}
