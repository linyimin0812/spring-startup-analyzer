package io.github.linyimin0812.async.processor;

import io.github.linyimin0812.async.bean.AsyncInitBeanHolder;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.util.Enumeration;


/**
 * @author yiminlin
 * @date 2023/05/14 15:32
 **/
public class AsyncBeanPriorityLoadPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        Enumeration<String> enumeration = AsyncInitBeanHolder.getAsyncInitBeanNames();

        while (enumeration.hasMoreElements()) {
            String beanName = enumeration.nextElement();
            logger.info("async init bean: {}", beanName);
            beanFactory.getBean(beanName);
        }
    }
}
