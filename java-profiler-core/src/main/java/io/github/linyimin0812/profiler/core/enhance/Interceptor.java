package io.github.linyimin0812.profiler.core.enhance;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import io.github.linyimin0812.Bridge;

/**
 * @author linyimin
 * @date 2023/04/17 21:39
 **/
public class Interceptor {

    @AtEnter
    public static void atEnter(@Binding.Class Class<?> clazz,
                               @Binding.This Object target,
                               @Binding.MethodName String methodName,
                               @Binding.MethodDesc String methodDesc,
                               @Binding.Args Object[] args) {
        Bridge.atEnter(clazz, target, methodName, methodDesc, args);
    }

    @AtExit
    public static void atExit(@Binding.Class Class<?> clazz,
                              @Binding.This Object target,
                              @Binding.MethodName String methodName,
                              @Binding.MethodDesc String methodDesc,
                              @Binding.Args Object[] args,
                              @Binding.Return Object returnObj) {
        Bridge.atExit(clazz, target, methodName, methodDesc, args, returnObj);
    }

    @AtExceptionExit
    public static void atExceptionExit(@Binding.Class Class<?> clazz,
                                       @Binding.This Object target,
                                       @Binding.MethodName String methodName,
                                       @Binding.MethodDesc String methodDesc,
                                       @Binding.Args Object[] args,
                                       @Binding.Throwable Throwable throwable) {
        Bridge.atExceptionExit(clazz, target, methodName, methodDesc, args, throwable);
    }
}
