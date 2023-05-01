package com.github.linyimin.profiler.api.event;

/**
 * @author linyimin
 * @date 2023/04/19 17:56
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
