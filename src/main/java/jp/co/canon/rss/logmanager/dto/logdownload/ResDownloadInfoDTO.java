package jp.co.canon.rss.logmanager.dto.logdownload;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ResDownloadInfoDTO {
    private Map<String, Map<String, List<String>>> lists;
}
