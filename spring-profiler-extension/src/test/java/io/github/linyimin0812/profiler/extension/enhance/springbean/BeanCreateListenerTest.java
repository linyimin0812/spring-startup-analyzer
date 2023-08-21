package io.github.linyimin0812.profiler.extension.enhance.springbean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
class BeanCreateListenerTest {

    private final BeanCreateListener beanCreateListener = new BeanCreateListener();

    @Test
    void filter() {
        assertTrue(beanCreateListener.filter("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"));

        assertFalse(beanCreateListener.filter("AbstractAutowireCapableBeanFactory"));
    }

    @Test
    void onEvent() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    void testFilter() {
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
    void listen() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    void start() {
        // TODO:
        System.out.println("// TODO:");
    }

    @Test
    void stop() {
        // TODO:
        System.out.println("// TODO:");
    }
}