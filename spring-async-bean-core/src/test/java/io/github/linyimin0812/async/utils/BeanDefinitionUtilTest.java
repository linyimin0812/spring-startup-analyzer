package io.github.linyimin0812.async.utils;

import io.github.linyimin0812.async.springbeans.SpringFactory;
import io.github.linyimin0812.async.springbeans.TestComponentBean;
import io.github.linyimin0812.async.springbeans.TestXmlBean;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class BeanDefinitionUtilTest {

    @Test
    public void isFromConfigurationSource() {
        boolean isFrom = BeanDefinitionUtil.isFromConfigurationSource(SpringFactory.getBeanFactory().getBeanDefinition("testComponentBean"));
        assertFalse(isFrom);
    }

    @Test
    public void resolveBeanClassType() {
        Class<?> clazz = BeanDefinitionUtil.resolveBeanClassType(SpringFactory.getBeanFactory().getBeanDefinition("testXmlBean"));
        assertEquals(TestXmlBean.class, clazz);

        clazz = BeanDefinitionUtil.resolveBeanClassType(SpringFactory.getBeanFactory().getBeanDefinition("testComponentBean"));

        assertEquals(TestComponentBean.class, clazz);
    }
}