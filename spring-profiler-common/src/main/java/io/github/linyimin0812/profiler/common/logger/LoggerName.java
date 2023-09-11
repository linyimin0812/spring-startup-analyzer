package io.github.linyimin0812.profiler.common.logger;

/**
 * @author linyimin
 **/
public enum LoggerName {
    /**
     * information about app startup
     */
    STARTUP("startup"),

    /**
     * enhanced method information
     */
    TRANSFORM("transform"),

    /**
     * async init method bean information
     */
    ASYNC_INIT_BEAN("async-init-bean");

    private final String value;

    private LoggerName(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
