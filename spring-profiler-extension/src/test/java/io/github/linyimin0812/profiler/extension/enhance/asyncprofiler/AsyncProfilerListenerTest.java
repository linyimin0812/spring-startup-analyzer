package io.github.linyimin0812.profiler.extension.enhance.asyncprofiler;

import org.junit.Assert;

/**
 * @author linyimin
 **/
public class AsyncProfilerListenerTest {

    private final AsyncProfilerListener asyncProfilerListener = new AsyncProfilerListener();
    @org.junit.Test
    public void filter() {
        Assert.assertFalse(asyncProfilerListener.filter(""));
    }

    @org.junit.Test
    public void testFilter() {
        Assert.assertTrue(asyncProfilerListener.filter("", new String[] {}));
    }

    @org.junit.Test
    public void onEvent() {

    }

    @org.junit.Test
    public void listen() {
        Assert.assertTrue(asyncProfilerListener.listen().isEmpty());
    }

    @org.junit.Test
    public void start() {
    }

    @org.junit.Test
    public void stop() {
    }
}