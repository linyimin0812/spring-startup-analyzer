package io.github.linyimin0812.profiler.extension.enhance.springbean;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author linyimin
 **/
public class BeanCreateListenerTest {

    private final BeanCreateListener beanCreateListener = new BeanCreateListener();

    @Test
    public void filter() {
        Assert.assertTrue(beanCreateListener.filter("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"));

        Assert.assertFalse(beanCreateListener.filter("AbstractAutowireCapableBeanFactory"));
    }

    @Test
    public void onEvent() {
    }

    @Test
    public void testFilter() {
        String listenMethodName = "createBean";
        String[] listenMethodTypes = new String[] {
                "java.lang.String",
                "org.springframework.beans.factory.support.RootBeanDefinition",
                "java.lang.Object[]"
        };

        Assert.assertTrue(beanCreateListener.filter(listenMethodName, listenMethodTypes));
        Assert.assertFalse(beanCreateListener.filter(listenMethodName, new String[] {}));

    }

    @Test
    public void listen() {
    }

    @Test
    public void start() {
    }

    @Test
    public void stop() {
    }
}