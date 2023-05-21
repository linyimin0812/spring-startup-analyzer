package io.github.linyimin0812.profiler.extension.enhance.invoke;

import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.jaeger.Jaeger;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.IpUtil;
import io.github.linyimin0812.profiler.common.utils.MainClassUtil;
import io.github.linyimin0812.profiler.extension.container.IocContainerHolder;
import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

import java.util.*;

/**
 * @author linyimin
 * @date 2023/04/21 21:33
 **/
@MetaInfServices(EventListener.class)
public class WholeChainListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private List<String> listenPackages = new ArrayList<>();

    private Tracer tracer;
    private Span ancestorSpan;

    private static final PersistentThreadLocal<Stack<Span>> parentStackThreadLocal = new PersistentThreadLocal<>(Stack::new);

    @Override
    public boolean filter(String className) {

        if (listenPackages.isEmpty()) {
            return false;
        }

        for (String packagePath : listenPackages) {
            if (className.contains(packagePath)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {

        if (event instanceof AtEnterEvent) {

            InvokeEvent invokeEvent = (InvokeEvent) event;
            // 调用开始
            Span span;

            if (!parentStackThreadLocal.get().isEmpty()) {

                Span parentSpan = parentStackThreadLocal.get().peek();

                span = tracer.spanBuilder(getName(invokeEvent))
                        .setAttribute("thread", Thread.currentThread().getName())
                        .setParent(Context.current().with(parentSpan))
                        .startSpan();
            } else {
                span = tracer.spanBuilder(getName(invokeEvent))
                        .setAttribute("thread", Thread.currentThread().getName())
                        .setParent(Context.current().with(ancestorSpan))
                        .startSpan();
            }

            parentStackThreadLocal.get().push(span);

        } else if (event instanceof AtExitEvent) {
            // 调用结束, 出栈
            Stack<Span> stack = parentStackThreadLocal.get();
            if (!stack.isEmpty()) {
                stack.pop().end();
            }
        }
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    private String getName(InvokeEvent invokeEvent) {

        String classLoader = invokeEvent.clazz.getClassLoader().getClass().getSimpleName();
        String className = invokeEvent.clazz.getName();
        String methodName = invokeEvent.methodName;

        return String.format("[%s] - %s.%s", classLoader, className, methodName);
    }

    @Override
    public void start() {
        logger.info("===============WholeChainListener start==================");

        if (!IpUtil.isJaegerReachable()) {
            logger.warn("Skip WholeChainListener for jaeger is not reachable.");
            return;
        }

        if (ProfilerSettings.contains("java-profiler.invoke.chain.packages")) {

            String packages = ProfilerSettings.getProperty("java-profiler.invoke.chain.packages", "");
            if (packages != null && packages.length() > 0) {
                listenPackages = Arrays.asList(packages.split(","));
            }
        } else {
            listenPackages = new ArrayList<>(MainClassUtil.getPackages());
        }

        if (listenPackages.isEmpty()) {
            logger.warn("WholeChainListener listen packages is empty.");
        } else {
            logger.info("WholeChainListener listen packages: {}", String.join(",", listenPackages));
        }

        tracer = IocContainerHolder.getContainer().getComponent(Jaeger.class).createTracer("app-start-up");
        ancestorSpan = tracer.spanBuilder("app-startup-span").startSpan();
    }

    @Override
    public void stop() {
        logger.info("===============WholeChainListener stop==================");

        if (!IpUtil.isJaegerReachable() || ancestorSpan == null) {
            return;
        }

        Collection<Stack<Span>> collection = parentStackThreadLocal.getAll();

        for (Stack<Span> stack : collection) {
            while (!stack.isEmpty()) {
                stack.pop().end();
            }
        }

        ancestorSpan.end();
    }
}
