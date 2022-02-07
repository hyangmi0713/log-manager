package jp.co.canon.rss.logmanager.dto.logdownload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ResMachineListDTO {
    private String equipment_name;
    private String fab_name;
    private String inner_tool_id;
    private String machineName;
    private String toolType;
    private String tool_id;
    private String tool_serial;
    private String user_name;
}
