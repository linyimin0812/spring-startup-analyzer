package io.github.linyimin0812.async.bean

import io.github.linyimin0812.async.config.AsyncBeanProperties
import io.github.linyimin0812.async.config.AsyncConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


/**
 * @author linyimin
 * */
@ContextConfiguration("classpath:bean-context.xml")
@TestPropertySource(locations = "classpath:application.properties")
class AsyncInitBeanFinderSpec extends Specification {

    @Autowired
    private ApplicationContext applicationContext;

    def "test getAsyncInitMethodName"() {

        given:
        AsyncBeanProperties properties = AsyncBeanProperties.parse(applicationContext.getEnvironment())
        AsyncConfig.getInstance().setAsyncBeanProperties(properties)
        String beanName = "testComponentBean"

        when:

        BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext)
                .getBeanFactory()
                .getBeanDefinition(beanName)

        then:
        AsyncInitBeanFinder.getAsyncInitMethodName(beanName, beanDefinition) == 'initTestComponentBean'
    }
}
