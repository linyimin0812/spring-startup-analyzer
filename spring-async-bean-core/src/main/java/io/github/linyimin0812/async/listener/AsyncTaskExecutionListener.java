package io.github.linyimin0812.async.listener;

import io.github.linyimin0812.async.executor.AsyncTaskExecutor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * @author linyimin
 **/
public class AsyncTaskExecutionListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware, PriorityOrdered {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (this.applicationContext.equals(event.getApplicationContext())) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
