package io.github.linyimin0812.async.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author linyimin
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:bean-context.xml")
public class AsyncTaskExecutionListenerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void setApplicationContext() {
        AsyncTaskExecutionListener listener = applicationContext.getBean(AsyncTaskExecutionListener.class);
        assertNotNull(listener.getApplicationContext());
    }

    @Test
    public void getOrder() {
        AsyncTaskExecutionListener listener = new AsyncTaskExecutionListener();
        assertEquals(Ordered.HIGHEST_PRECEDENCE, listener.getOrder());
    }
}