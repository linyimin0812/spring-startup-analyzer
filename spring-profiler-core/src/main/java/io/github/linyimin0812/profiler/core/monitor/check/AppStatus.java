package io.github.linyimin0812.profiler.core.monitor.check;

/**
 * @author linyimin
 * @date 2023/04/20 17:41
 * @description app状态
 **/
public enum AppStatus {
    /**
     * 启动中
     */
    initializing,
    /**
     * 运行中
     */
    running,

    /**
     * 启动失败
     */
    failed

}
