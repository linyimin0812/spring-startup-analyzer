package io.github.linyimin0812.profiler.extension.enhance.sample;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
class AsyncProfilerListenerTest {

    private final AsyncProfilerListener asyncProfilerListener = new AsyncProfilerListener();
    @Test
    void filter() {
        assertFalse(asyncProfilerListener.filter(""));
    }

    @Test
    void testFilter() { assertTrue(asyncProfilerListener.filter("", new String[] {}));
    }

    @Test
    void onEvent() {
        // TODO:
        System.out.println("// TODO:");

    }

    @Test
    void listen() {
        assertTrue(asyncProfilerListener.listen().isEmpty());
    }

    @Test
    void start() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    void stop() {
        // TODO:
        System.out.println("// TODO:");
    }
}