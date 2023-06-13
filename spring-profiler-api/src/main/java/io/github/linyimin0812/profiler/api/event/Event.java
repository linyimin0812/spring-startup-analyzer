package io.github.linyimin0812.profiler.api.event;

/**
 * @author linyimin
 **/
public abstract class Event {

    public final Type type;

    protected Event(Type type) {
        this.type = type;
    }

    public enum Type {
        /**
         * 函数入口
         */
        AT_ENTER,

        /**
         * 函数退出
         */
        AT_EXIT,

        /**
         * 函数抛出异常
         */
        AT_EXCEPTION_EXIT
    }

}
