package io.github.linyimin0812.profiler.common.ui

import spock.lang.Specification

/**
 * @author linyimin
 * */
class BeanInitResultSpec extends Specification {

    def "test getId"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        then:
        beanInitResult.getId() >= 1000
    }

    def "test getParentId"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")

        when:
        beanInitResult.addChild(childBeanInitResult)

        then:
        childBeanInitResult.getParentId() == beanInitResult.getId()
    }

    def "test getName"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")

        then:
        "test" == beanInitResult.getName()
    }


    def "test getDuration"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")

        when:
        beanInitResult.duration()

        then:
        beanInitResult.getDuration() >= 0
    }

    def "test getActualDuration"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
        beanInitResult.addChild(childBeanInitResult)

        when:
        childBeanInitResult.duration()
        beanInitResult.duration()

        then:
        beanInitResult.getActualDuration() < 10
    }

    def "test duration"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
        beanInitResult.addChild(childBeanInitResult)

        when:
        childBeanInitResult.duration()
        beanInitResult.duration()

        then:
        beanInitResult.getDuration() == beanInitResult.getActualDuration() + childBeanInitResult.getDuration()

    }

    def "test getTags"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        Map<String, String> tags = new HashMap<>()
        tags.put("class", "Test")

        when:
        beanInitResult.setTags(tags)

        then:
        beanInitResult.getTags().containsKey("class")

    }

    def "test getChildren"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")

        when:
        beanInitResult.addChild(childBeanInitResult)

        then:
        beanInitResult.getChildren().size() == 1

    }
}
