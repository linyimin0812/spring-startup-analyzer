package io.github.linyimin0812.async.utils;

import io.github.linyimin0812.async.springbeans.SpringFactory;
import io.github.linyimin0812.async.springbeans.TestComponentBean;
import io.github.linyimin0812.async.springbeans.TestXmlBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author linyimin
 **/
class BeanDefinitionUtilTest {

    @Test
    void isFromConfigurationSource() {
        boolean isFrom = BeanDefinitionUtil.isFromConfigurationSource(SpringFactory.getBeanFactory().getBeanDefinition("testComponentBean"));
        assertFalse(isFrom);
    }

    @Test
    void resolveBeanClassType() {
        Class<?> clazz = BeanDefinitionUtil.resolveBeanClassType(SpringFactory.getBeanFactory().getBeanDefinition("testXmlBean"));
        assertEquals(TestXmlBean.class, clazz);

        clazz = BeanDefinitionUtil.resolveBeanClassType(SpringFactory.getBeanFactory().getBeanDefinition("testComponentBean"));

        assertEquals(TestComponentBean.class, clazz);
    }
}