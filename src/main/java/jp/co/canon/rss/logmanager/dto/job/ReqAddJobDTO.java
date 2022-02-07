package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class ReqAddJobDTO {
    private List<Map<String, String>> steps;
}