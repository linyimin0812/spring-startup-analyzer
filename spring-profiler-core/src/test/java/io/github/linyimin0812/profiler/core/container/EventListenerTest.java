package io.github.linyimin0812.profiler.core.container;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExceptionExitEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linyimin
 * only for IocContainer test
 **/
@MetaInfServices
public class EventListenerTest implements EventListener {

    public static AtEnterEvent atEnterEvent;
    public static AtExitEvent atExitEvent;
    public static AtExceptionExitEvent atExceptionExitEvent;

    @Override
    public boolean filter(String className) {
        return "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory".equals(className)
                || "java.net.URLClassLoader".equals(className);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        if (!"createBean".equals(methodName) && !"findResource".equals(methodName)) {
            return false;
        }

        if (methodTypes == null) {
            return false;
        }

        if (methodTypes.length == 3) {
            return "java.lang.String".equals(methodTypes[0]) && "org.springframework.beans.factory.support.RootBeanDefinition".equals(methodTypes[1]) && "java.lang.Object[]".equals(methodTypes[2]);
        }

        if (methodTypes.length == 1) {
            return "java.lang.String".equals(methodTypes[0]);
        }

        return false;

    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof AtEnterEvent) {
            atEnterEvent = (AtEnterEvent) event;
        }

        if (event instanceof AtExitEvent) {
            atExitEvent = (AtExitEvent) event;
        }

        if (event instanceof AtExceptionExitEvent) {
            atExceptionExitEvent = (AtExceptionExitEvent) event;
        }
    }

    @Override
    public List<Event.Type> listen() {

        List<Event.Type> types = new ArrayList<>();
        types.add(Event.Type.AT_ENTER);
        types.add(Event.Type.AT_EXIT);
        types.add(Event.Type.AT_EXCEPTION_EXIT);

        return types;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
