/*
 * Copyright The Arthas Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.linyimin0812;

/**
 * @author linyimin
 * @date 2023/04/18 15:25
 **/
public class Bridge {

    public static final AbstractBridge NOP_BRIDGE = new NopBridge();

    private static AbstractBridge bridgeInstance = NOP_BRIDGE;

    public static AbstractBridge getBridge() {
        return bridgeInstance;
    }

    public static void setBridge(AbstractBridge bridge) {
        bridgeInstance = bridge;
    }

    public static void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {
        bridgeInstance.atEnter(clazz, target, methodName, methodDesc, args);
    }

    public static void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {
        bridgeInstance.atExit(clazz, target, methodName, methodDesc, args, returnObject);
    }

    public static void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {
        bridgeInstance.atExceptionExit(clazz, target, methodName, methodDesc, args, throwable);
    }

    public static abstract class AbstractBridge {
        public abstract void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args);

        public abstract void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject);

        public abstract void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable);
    }

    static class NopBridge extends AbstractBridge {

        @Override
        public void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {
        }

        @Override
        public void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {

        }

        @Override
        public void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {

        }
    }
}
