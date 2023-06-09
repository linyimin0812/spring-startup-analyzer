package io.github.linyimin0812.profiler.api.event;

/**
 * @author linyimin
 * @date 2023/04/19 20:54
 **/
public class AtExceptionExitEvent extends InvokeEvent {

    public final Throwable throwable;

    public AtExceptionExitEvent(long processId,
                                long invokeId,
                                Class<?>  clazz,
                                Object target,
                                String methodName,
                                String methodDesc,
                                Object[] args,
                                Throwable throwable) {
        super(processId, invokeId, clazz, target, methodName, methodDesc, args, Type.AT_EXCEPTION_EXIT);

        this.throwable = throwable;
    }
}
