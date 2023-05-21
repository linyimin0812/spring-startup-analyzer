package io.github.linyimin0812.profiler.extension.enhance.springbean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.utils.IpUtil;
import io.github.linyimin0812.profiler.extension.container.IocContainerHolder;
import io.github.linyimin0812.profiler.extension.enhance.invoke.PersistentThreadLocal;
import io.github.linyimin0812.profiler.common.jaeger.Jaeger;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

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

    private final PersistentThreadLocal<Stack<Span>> parentStackThreadLocal = new PersistentThreadLocal<>(Stack::new);

    private final PersistentThreadLocal<Stack<BeanCreateResult>> parentBeanStackThreadLocal = new PersistentThreadLocal<>(Stack::new);

    public static final List<BeanCreateResult> BEAN_CREATE_RESULTS = new ArrayList<>();

    @Override
    public boolean filter(String className) {
        if (!IpUtil.isJaegerReachable()) {
            return false;
        }
        return super.filter(className);
    }

    @Override
    public void onEvent(Event event) {

        if (event.type == Event.Type.AT_ENTER) {
            AtEnterEvent atEnterEvent = (AtEnterEvent) event;
            // 记录bean初始化开始
            String beanName = (String) atEnterEvent.args[0];

            addSpan(beanName);
            addBeanCreateResult(beanName);

        } else if (event.type == Event.Type.AT_EXIT) {
            // bean初始化结束, 出栈
            parentStackThreadLocal.get().pop().end();
            BEAN_CREATE_RESULTS.add(parentBeanStackThreadLocal.get().pop());
        }
    }

    private void addSpan(String beanName) {
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
    }

    private void addBeanCreateResult(String beanName) {

        BeanCreateResult beanCreateResult = new BeanCreateResult(beanName);

        if (!parentBeanStackThreadLocal.get().isEmpty()) {

            BeanCreateResult parentBeanResult = parentBeanStackThreadLocal.get().peek();
            beanCreateResult.setHasParent(true);
            parentBeanResult.addChild(beanCreateResult);

        }

        parentBeanStackThreadLocal.get().push(beanCreateResult);
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

        if (!IpUtil.isJaegerReachable()) {
            logger.warn("Skip BeanCreateListener for jaeger is not reachable");
            return;
        }

        tracer = IocContainerHolder.getContainer().getComponent(Jaeger.class).createTracer("spring-bean-load-tracer");
        ancestorSpan = tracer.spanBuilder("bean-create-span").startSpan();
    }

    @Override
    public void stop() {
        logger.info("============BeanCreateListener stop=============");
        if (!IpUtil.isJaegerReachable() || ancestorSpan == null) {
            return;
        }
        ancestorSpan.end();
    }

    public static class BeanCreateResult {
        private String beanName;
        private long beanStartTime;
        private long beanEndTime;
        private String loadThreadName;

        private boolean hasParent;

        private List<BeanCreateResult> children;

        public BeanCreateResult(String beanName) {
            this.beanName = beanName;
            this.beanStartTime = System.currentTimeMillis();
            this.loadThreadName = Thread.currentThread().getName();
            this.children = new ArrayList<>();
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public long getBeanStartTime() {
            return beanStartTime;
        }

        public void setBeanStartTime(long beanStartTime) {
            this.beanStartTime = beanStartTime;
        }

        public List<BeanCreateResult> getChildren() {
            return children;
        }

        public void setChildren(List<BeanCreateResult> children) {
            this.children = children;
        }

        public String getLoadThreadName() {
            return loadThreadName;
        }

        public void setLoadThreadName(String loadThreadName) {
            this.loadThreadName = loadThreadName;
        }

        public long getBeanEndTime() {
            return beanEndTime;
        }

        public void setBeanEndTime(long beanEndTime) {
            this.beanEndTime = beanEndTime;
        }

        public boolean isHasParent() {
            return hasParent;
        }

        public void setHasParent(boolean hasParent) {
            this.hasParent = hasParent;
        }

        public void addChild(BeanCreateResult child) {
            this.children.add(child);
        }
    }
}
