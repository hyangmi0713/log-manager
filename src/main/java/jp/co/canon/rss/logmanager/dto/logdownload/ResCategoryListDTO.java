package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResCategoryListDTO {
    private String auto;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String dest;
    private String display;
    private String fileName;
    private String filePath;
    private String port;
}
