package io.github.linyimin0812.async;

import io.github.linyimin0812.async.listener.AsyncTaskExecutionListener;
import io.github.linyimin0812.async.processor.AsyncBeanPriorityLoadPostProcessor;
import io.github.linyimin0812.async.processor.AsyncProxyBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiminlin
 **/
@Configuration
@EnableConfigurationProperties(AsyncSpringBeanProperties.class)
public class AsyncBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static AsyncTaskExecutionListener asyncTaskExecutionListener() {
        return new AsyncTaskExecutionListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor() {
        return new AsyncProxyBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncBeanPriorityLoadPostProcessor asyncBeanPriorityLoadPostProcessor() {
        return new AsyncBeanPriorityLoadPostProcessor();
    }
}
