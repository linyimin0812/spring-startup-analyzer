package io.github.linyimin0812.profiler.api.event;

/**
 * @author linyimin
 **/
public class AtExitEvent extends InvokeEvent {

    public final Object returnObj;

    public AtExitEvent(long processId,
                       long invokeId,
                       Class<?>  clazz,
                       Object target,
                       String methodName,
                       String methodDesc,
                       Object[] args,
                       Object returnObj) {
        super(processId, invokeId, clazz, target, methodName, methodDesc, args, Type.AT_EXIT);
        this.returnObj = returnObj;
    }
}
