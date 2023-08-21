package io.github.linyimin0812.profiler.extension.enhance.sample;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
public class AsyncProfilerListenerTest {

    private final AsyncProfilerListener asyncProfilerListener = new AsyncProfilerListener();
    @Test
    public void filter() {
        assertFalse(asyncProfilerListener.filter(""));
    }

    @Test
    public void testFilter() { assertTrue(asyncProfilerListener.filter("", new String[] {}));
    }

    @Test
    public void onEvent() {
        // TODO:
        System.out.println("// TODO:");

    }

    @Test
    public void listen() {
        assertTrue(asyncProfilerListener.listen().isEmpty());
    }

    @Test
    public void start() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    public void stop() {
        // TODO:
        System.out.println("// TODO:");
    }
}