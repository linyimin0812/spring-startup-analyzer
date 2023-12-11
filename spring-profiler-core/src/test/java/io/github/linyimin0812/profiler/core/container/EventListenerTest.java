package io.github.linyimin0812.profiler.core.container;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * @author linyimin
 * only for IocContainer test
 **/
@MetaInfServices
public class EventListenerTest implements EventListener {
    @Override
    public boolean filter(String className) {
        return "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory".equals(className);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        if (!"createBean".equals(methodName)) {
            return false;
        }

        if (methodTypes == null || methodTypes.length != 3) {
            return false;
        }

        return "java.lang.String".equals(methodTypes[0]) && "org.springframework.beans.factory.support.RootBeanDefinition".equals(methodTypes[1]) && "java.lang.Object[]".equals(methodTypes[2]);
    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public List<Event.Type> listen() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
