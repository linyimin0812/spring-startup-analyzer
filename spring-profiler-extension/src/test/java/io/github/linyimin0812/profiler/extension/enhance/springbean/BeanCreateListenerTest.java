package io.github.linyimin0812.profiler.extension.enhance.springbean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
public class BeanCreateListenerTest {

    private final BeanCreateListener beanCreateListener = new BeanCreateListener();

    @Test
    public void filter() {
        assertTrue(beanCreateListener.filter("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"));

        assertFalse(beanCreateListener.filter("AbstractAutowireCapableBeanFactory"));
    }

    @Test
    public void onEvent() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    public void testFilter() {
        String listenMethodName = "createBean";
        String[] listenMethodTypes = new String[] {
                "java.lang.String",
                "org.springframework.beans.factory.support.RootBeanDefinition",
                "java.lang.Object[]"
        };

        assertTrue(beanCreateListener.filter(listenMethodName, listenMethodTypes));
        assertFalse(beanCreateListener.filter(listenMethodName, new String[] {}));

    }

    @Test
    public void listen() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    public void start() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    public void stop() {
        // TODO:
        System.out.println("// TODO:");
    }
}