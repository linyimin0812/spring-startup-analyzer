package io.github.linyimin0812.async.springbeans;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * @author linyimin
 **/
public class SpringFactory {

    private static final DefaultListableBeanFactory beanFactory;

    static {
        beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("bean-context.xml");
    }
    public static DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
