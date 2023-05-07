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
public class BeanCreateListener extends BeanListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private Tracer tracer;
    private Span ancestorSpan;

    private static final PersistentThreadLocal<Stack<Span>> parentStackThreadLocal = new PersistentThreadLocal<>(Stack::new);

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
    public String getMethodName() {
        return "createBean";
    }

    @Override
    public String[] getMethodTypes() {
        return new String[] {
                "java.lang.String",
                "org.springframework.beans.factory.support.RootBeanDefinition",
                "java.lang.Object[]"
        };
    }

    @Override
    public void start() {
        logger.info("============BeanCreateListener start=============");
        tracer = IocContainerHolder.getContainer().getComponent(Jaeger.class).createTracer("spring-bean-load-tracer");
        ancestorSpan = tracer.spanBuilder("bean-create-span").startSpan();
    }

    @Override
    public void stop() {
        logger.info("============BeanCreateListener stop=============");
        ancestorSpan.end();
    }
}
