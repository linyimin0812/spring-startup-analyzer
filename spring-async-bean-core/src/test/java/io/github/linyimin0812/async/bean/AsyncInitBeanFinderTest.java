package io.github.linyimin0812.async.bean;

import io.github.linyimin0812.async.config.AsyncBeanProperties;
import io.github.linyimin0812.async.config.AsyncConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:bean-context.xml")
@TestPropertySource(locations = {"classpath:application.properties"})
public class AsyncInitBeanFinderTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void  init() {
        AsyncBeanProperties properties = AsyncBeanProperties.parse(applicationContext.getEnvironment());
        AsyncConfig.getInstance().setAsyncBeanProperties(properties);
    }

    @org.junit.Test
    public void getAsyncInitMethodName() {

        String beanName = "testComponentBean";

        BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext)
                .getBeanFactory()
                .getBeanDefinition(beanName);

        assertEquals("initTestComponentBean", AsyncInitBeanFinder.getAsyncInitMethodName(beanName, beanDefinition));
    }
}