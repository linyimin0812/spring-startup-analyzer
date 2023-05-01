package com.github.linyimin.profiler.extension.container;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author linyimin
 * @date 2023/04/13 11:45
 * @description Container holder
 **/
public class IocContainerHolder {

    private static final AtomicBoolean started = new AtomicBoolean();
    private static final AtomicBoolean stopped = new AtomicBoolean();

    private static ComponentContainer container;

    public static void start() {
        if (started.compareAndSet(false, true)) {
            container = new ComponentContainer();
            container.start();
        }
    }

    public static void stop() {
        if (stopped.compareAndSet(false, true)) {
            container.stop();
        }
    }

    public static ComponentContainer getContainer() {
        if (container == null && started.compareAndSet(false, true)) {
            start();
        }
        return container;
    }

}
