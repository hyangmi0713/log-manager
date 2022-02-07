package jp.co.canon.rss.logmanager.dto.job;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import ch.qos.logback.classic.Logger;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResMakeLogger {
    Logger logger;
    String rid;
}
