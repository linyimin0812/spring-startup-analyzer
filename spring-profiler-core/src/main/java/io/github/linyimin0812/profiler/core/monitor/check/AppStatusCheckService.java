package io.github.linyimin0812.profiler.core.monitor.check;

/**
 * 检查应用状态
 * @author linyimin
 **/
public interface AppStatusCheckService {

    default void init() {}

    AppStatus check();
}
