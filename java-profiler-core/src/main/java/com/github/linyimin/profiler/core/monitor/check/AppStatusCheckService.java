package com.github.linyimin.profiler.core.monitor.check;

/**
 * @author linyimin
 * @date 2023/04/20 17:51
 * 检查应用状态
 **/
public interface AppStatusCheckService {

    default void init() {}

    AppStatus check();
}
