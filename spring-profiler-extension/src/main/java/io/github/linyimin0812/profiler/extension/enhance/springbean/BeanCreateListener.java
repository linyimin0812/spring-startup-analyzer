package io.github.linyimin0812.profiler.extension.enhance.springbean;

import com.alibaba.fastjson.JSON;
import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.ui.BeanInitResult;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

import java.util.*;

/**
 * @author linyimin
 * @date 2023/04/18 17:05
 **/
@MetaInfServices(EventListener.class)
public class BeanCreateListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private final PersistentThreadLocal<Stack<BeanInitResult>> profilerResultThreadLocal = new PersistentThreadLocal<Stack<BeanInitResult>>(Stack::new);

    @Override
    public boolean filter(String className) {
        return "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory".equals(className);
    }

    @Override
    public void onEvent(Event event) {

        if (event.type == Event.Type.AT_ENTER) {
            AtEnterEvent atEnterEvent = (AtEnterEvent) event;
            // 记录bean初始化开始
            String beanName = (String) atEnterEvent.args[0];
            createBeanInitResult(beanName);

        } else if (event.type == Event.Type.AT_EXIT) {
            // bean初始化结束, 出栈

            AtExitEvent atExitEvent = (AtExitEvent) event;
            Map<String, String> tags = new HashMap<>();
            tags.put("threadName", Thread.currentThread().getName());
            tags.put("class", atExitEvent.returnObj.getClass().getName());
            ClassLoader classLoader = atExitEvent.returnObj.getClass().getClassLoader();
            tags.put("classloader", classLoader == null ? "boostrap" : classLoader.getClass().getSimpleName());

            BeanInitResult beanInitResult = profilerResultThreadLocal.get().pop();
            beanInitResult.setTags(tags);
            beanInitResult.duration();
        }
    }

    private void createBeanInitResult(String beanName) {

        BeanInitResult beanInitResult = new BeanInitResult(beanName);

        StartupVO.addBeanInitResult(beanInitResult);

        if (!profilerResultThreadLocal.get().isEmpty()) {

            BeanInitResult parentBeanInitResult = profilerResultThreadLocal.get().peek();
            parentBeanInitResult.addChild(beanInitResult);

        }

        profilerResultThreadLocal.get().push(beanInitResult);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {

        String listenMethodName = "createBean";
        String[] listenMethodTypes = new String[] {
                "java.lang.String",
                "org.springframework.beans.factory.support.RootBeanDefinition",
                "java.lang.Object[]"
        };

        if (!listenMethodName.equals(methodName)) {
            return false;
        }
        if (methodTypes == null || listenMethodTypes.length != methodTypes.length) {
            return false;
        }

        for (int i = 0; i < listenMethodTypes.length; i++) {
            if (!listenMethodTypes[i].equals(methodTypes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info("============BeanCreateListener start=============");
    }

    @Override
    public void stop() {
        logger.info("============BeanCreateListener stop=============");
        if (!profilerResultThreadLocal.getAll().isEmpty()) {
            logger.warn("profilerResultThreadLocal is not empty. There may be a problem with the initialization of the bean. {}", JSON.toJSONString(profilerResultThreadLocal.getAll()));
        }
    }
}
