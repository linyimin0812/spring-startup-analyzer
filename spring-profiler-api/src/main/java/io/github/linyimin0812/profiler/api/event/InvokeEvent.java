package io.github.linyimin0812.profiler.api.event;

/**
 * @author linyimin
 * @date 2023/04/19 20:38
 **/
public abstract class InvokeEvent extends Event {

    public final long processId;
    public final long invokeId;

    /**
     * 触发调用事件的类
     */
    public final Class<?> clazz;

    /**
     * 触发调用事件的方法名称
     */
    public final String methodName;

    /**
     * 触发调用事件的方法签名
     */
    public final String methodDesc;

    /**
     * 触发调用事件的方法参数
     */
    public final Object[] args;

    /**
     * 触发调用事件的对象
     */
    public final Object target;

    protected InvokeEvent(long processId,
                          long invokeId,
                          Class<?>  clazz,
                          Object target,
                          String methodName,
                          String methodDesc,
                          Object[] args,
                          Type type) {
        super(type);
        this.processId = processId;
        this.invokeId = invokeId;
        this.clazz = clazz;
        this.target = target;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.args = args;
    }
}
