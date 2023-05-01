package com.github.linyimin.profiler.extension.container;


import com.github.linyimin.profiler.common.jaeger.Jaeger;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author linyimin
 * @date 2023/04/13 11:45
 * @description 实例容器
 **/
public class ComponentContainer {

    private final MutablePicoContainer container = new PicoBuilder().withSetterInjection().withCaching().withLifecycle().build();

    private final AtomicBoolean started = new AtomicBoolean();

    private final AtomicBoolean stopped = new AtomicBoolean();

    public ComponentContainer() {
        // 服务在这里添加，接口和实现
        container.addComponent(Jaeger.class, Jaeger.class);

    }

    public <T> T getComponent(Class<T> clazz) {
        return container.getComponent(clazz);
    }

    /**
     * 启动服务容器
     */
    public final void start() {
        if (started.compareAndSet(false, true)) {
            container.start();
        }
    }

    /**
     * 停止容器
     */
    public final void stop() {
        if (stopped.compareAndSet(false, true)) {
            container.dispose();
        }
    }

    /**
     * 是否启动了
     *
     * @return 服务容器是否启动
     */
    public final boolean isStarted() {
        return started.get();
    }
}
