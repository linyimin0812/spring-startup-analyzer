package io.github.linyimin0812.async.bean;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiminlin
 * @date 2023/05/14 01:37
 **/
public class AsyncInitBeanHolder {
    private static final ConcurrentHashMap<String/*beanName*/, String/*methodName*/> ASYNC_BEANS_MAP = new ConcurrentHashMap<>(8);

    public static void registerAsyncInitBean(String beanName, String methodName) {
        if (beanName == null || methodName == null) {
            return;
        }

        ASYNC_BEANS_MAP.put(beanName, methodName);
    }

    public static String getAsyncInitMethodName(String beanName) {
        return ASYNC_BEANS_MAP.get(beanName);
    }

    public static Enumeration<String> getAsyncInitBeanNames() {
        return ASYNC_BEANS_MAP.keys();
    }
}
