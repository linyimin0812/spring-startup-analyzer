package io.github.linyimin0812.profiler.api.event;

/**
 * @author linyimin
 **/
public class AtEnterEvent extends InvokeEvent {

    public AtEnterEvent(long processId,
                        long invokeId,
                        Class<?>  clazz,
                        Object target,
                        String methodName,
                        String methodDesc,
                        Object[] args) {
        super(processId, invokeId, clazz, target, methodName, methodDesc, args, Type.AT_ENTER);

    }

    public AtEnterEvent changeParameter(int index, Object changeValue) {
        args[index] = changeValue;
        return this;
    }
}
