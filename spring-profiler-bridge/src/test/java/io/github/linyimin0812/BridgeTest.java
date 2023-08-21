package io.github.linyimin0812;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author linyimin
 **/
class BridgeTest {

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
    }

    @Test
    void atExit() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        Bridge.atExit(null, null, null, null, null, null);
        Bridge.setBridge(new BridgeImplTest());
        Bridge.atExit(null, null, null, null, null, null);
    }

    @Test
    void atExceptionExit() {
        Bridge.setBridge(Bridge.NOP_BRIDGE);
        Bridge.atExceptionExit(null, null, null, null, null, null);
        Bridge.setBridge(new BridgeImplTest());
        Bridge.atExceptionExit(null, null, null, null, null, null);
    }

    private static class BridgeImplTest extends Bridge.AbstractBridge {

        @Override
        public void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {
            System.out.println("BridgeImplTest.atEnter");
        }

        @Override
        public void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {
            System.out.println("BridgeImplTest.atExit");
        }

        @Override
        public void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {
            System.out.println("BridgeImplTest.atExceptionExit");
        }
    }
}