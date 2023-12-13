package io.github.linyimin0812.async.config

import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncConfigSpec extends Specification {

    def "test getInstance"() {
        when:
        AsyncConfig instance = AsyncConfig.getInstance()

        then:
        instance
    }

    def "test getAsyncBeanProperties"() {

        given:
        AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesSpec.parsePropertiesFromFile()

        when:
        AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties)

        then:
        AsyncConfig.getInstance().getAsyncBeanProperties() == asyncBeanProperties
    }

   def "test setAsyncBeanProperties"() {

       given:
       AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesSpec.parsePropertiesFromFile()

       when:
       AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties)

       then:
       AsyncConfig.getInstance().getAsyncBeanProperties() == asyncBeanProperties
   }

   def "test isAsyncBean"() {
       when:
       AsyncConfig.getInstance().setAsyncBeanProperties(AsyncBeanPropertiesSpec.parsePropertiesFromFile())

       then:
       AsyncConfig.getInstance().isAsyncBean("testBean")
   }
}
