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
        return false;
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
