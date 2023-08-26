package io.github.linyimin0812;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
class BridgeTest {

    private static boolean atEnter = false;
    private static boolean atExit = false;

    private static boolean atException = false;

    @Test
    void getBridge() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        assertEquals(Bridge.NOP_BRIDGE, Bridge.getBridge());
    }

    @Test
    void setBridge() {
        BridgeImplTest bridgeImplTest = new BridgeImplTest();
        Bridge.setBridge(bridgeImplTest);

        assertEquals(bridgeImplTest, Bridge.getBridge());
    }

    @Test
    void atEnter() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        Bridge.atEnter(null, null, null, null, null);
        Bridge.setBridge(new BridgeImplTest());
        Bridge.atEnter(null, null, null, null, null);
        assertTrue(atEnter);
    }

    @Test
    void atExit() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        Bridge.atExit(null, null, null, null, null, null);
        Bridge.setBridge(new BridgeImplTest());
        Bridge.atExit(null, null, null, null, null, null);
        assertTrue(atExit);
    }

    @Test
    void atExceptionExit() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        Bridge.atExceptionExit(null, null, null, null, null, null);
        Bridge.setBridge(new BridgeImplTest());
        Bridge.atExceptionExit(null, null, null, null, null, null);
        assertTrue(atException);
    }

    private static class BridgeImplTest extends Bridge.AbstractBridge {

        @Override
        public void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {
            atEnter = true;
            System.out.println("BridgeImplTest.atEnter");
        }

        @Override
        public void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {
            atExit = true;
            System.out.println("BridgeImplTest.atExit");
        }

        @Override
        public void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {
            atException = true;
            System.out.println("BridgeImplTest.atExceptionExit");
        }
    }
}