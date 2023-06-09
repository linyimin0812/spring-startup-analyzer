package io.github.linyimin0812.profiler.extension.enhance.springbean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;

import java.util.Arrays;
import java.util.List;

/**
 * @author linyimin
 * @date 2023/05/07 14:51
 **/
public abstract class BeanListener implements EventListener {

    protected final String className = "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory";

    public abstract String getMethodName();
    public abstract String[] getMethodTypes();

    @Override
    public boolean filter(String className) {
        return this.className.equals(className);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        if (!this.getMethodName().equals(methodName)) {
            return false;
        }
        if (methodTypes == null || this.getMethodTypes().length != methodTypes.length) {
            return false;
        }

        for (int i = 0; i < this.getMethodTypes().length; i++) {
            if (!this.getMethodTypes()[i].equals(methodTypes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }
}
