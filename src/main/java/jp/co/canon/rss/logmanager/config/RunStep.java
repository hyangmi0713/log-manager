package jp.co.canon.rss.logmanager.config;

public class RunStep {
    // StepType
    public static final String STEPTYPE_COLLECT = "collect";
    public static final String STEPTYPE_CONVERT = "convert";
    public static final String STEPTYPE_SUMMARY = "summary";
    public static final String STEPTYPE_CRAS = "cras";
    public static final String STEPTYPE_VERSION = "version";
    public static final String STEPTYPE_PURGE = "purge";
    public static final String STEPTYPE_CUSTOM = "custom";
    public static final String STEPTYPE_NOTICE = "notice";

    // Mode
    public static final String MODE_CYCLE = "cycle";
    public static final String MODE_TIME = "time";
    public static final String MODE_PRE = "pre";
    public static final String MODE_NEXT = "next";

    // Cycle
    public static final String CYCLE_MINUTE = "minute";
    public static final String CYCLE_HOUR = "hour";
    public static final String CYCLE_DAY = "day";

    // Status
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_NODATA = "nodata";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_NOTBUILD = "notbuild";
    public static final String STATUS_TIMEOUT = "timeout";

    // Status Cras
    public static final String STATUS_CRAS_SUCCESS = "success";
    public static final String STATUS_CRAS_ERROR = "error";
    public static final String STATUS_CRAS_CANCEL = "cancel";
    //TODO : idle로 변경 필요
    public static final String STATUS_CRAS_IDLE = "unknown";
    public static final String STATUS_CRAS_RUNNING = "running";
    public static final String STATUS_CRAS_NODATA = "nodata";

    // Type
    public static final String TYPE_REMOTE = "remote";
    public static final String TYPE_LOCAL = "local";

    // Manage Job in Cras
    public static final String MANAGE_CRAS_POST = "post";
    public static final String MANAGE_CRAS_PATCH = "patch";
    public static final String MANAGE_CRAS_DELETE = "delete";

    // Remote Job Build Queue
    public static final int JOB_TIMELINE = 100;
}
