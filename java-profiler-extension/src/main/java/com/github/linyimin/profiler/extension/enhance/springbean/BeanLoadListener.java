package com.github.linyimin.profiler.extension.enhance.springbean;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.api.EventListener;
import com.github.linyimin.profiler.api.event.AtEnterEvent;
import com.github.linyimin.profiler.api.event.Event;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.extension.container.IocContainerHolder;
import com.github.linyimin.profiler.extension.enhance.invoke.PersistentThreadLocal;
import com.github.linyimin.profiler.common.jaeger.Jaeger;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.kohsuke.MetaInfServices;

import java.util.*;

/**
 * @author linyimin
 * @date 2023/04/18 17:05
 **/
@MetaInfServices(EventListener.class)
public class BeanLoadListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private final String className = "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory";
    private final String methodName = "createBean";
    private final String[] methodTypes = new String[] {
        "java.lang.String",
        "org.springframework.beans.factory.support.RootBeanDefinition",
        "java.lang.Object[]"
    };

    private Tracer tracer;
    private Span ancestorSpan;

    private static final PersistentThreadLocal<Stack<Span>> parentStackThreadLocal = new PersistentThreadLocal<>(Stack::new);


    @Override
    public boolean filter(String className) {
        return this.className.equals(className);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        if (!this.methodName.equals(methodName)) {
            return false;
        }

        if (methodTypes == null || this.methodTypes.length != methodTypes.length) {
            return false;
        }

        for (int i = 0; i < this.methodTypes.length; i++) {
            if (!this.methodTypes[i].equals(methodTypes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onEvent(Event event) {
        if (event.type == Event.Type.AT_ENTER) {
            AtEnterEvent atEnterEvent = (AtEnterEvent) event;
            // 记录bean初始化开始
            String beanName = (String) atEnterEvent.args[0];

            Span span;

            if (!parentStackThreadLocal.get().isEmpty()) {

                Span parentSpan = parentStackThreadLocal.get().peek();

                span = tracer.spanBuilder(beanName)
                        .setAttribute("thread", Thread.currentThread().getName())
                        .setParent(Context.current().with(parentSpan))
                        .startSpan();
            } else {
                span = tracer.spanBuilder(beanName)
                        .setAttribute("thread", Thread.currentThread().getName())
                        .setParent(Context.current().with(ancestorSpan))
                        .startSpan();
            }

            parentStackThreadLocal.get().push(span);

        } else if (event.type == Event.Type.AT_EXIT) {
            // bean初始化结束, 出栈
            parentStackThreadLocal.get().pop().end();
        }
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info("============BeanLoadListener start=============");
        tracer = IocContainerHolder.getContainer().getComponent(Jaeger.class).createTracer("spring-bean-load-tracer");
        ancestorSpan = tracer.spanBuilder("bean-create-span").startSpan();
    }

    @Override
    public void stop() {
        logger.info("============BeanLoadListener stop=============");
        ancestorSpan.end();
    }
}
