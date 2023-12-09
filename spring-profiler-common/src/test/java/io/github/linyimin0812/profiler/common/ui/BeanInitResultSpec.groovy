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
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
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
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        beanInitResult.duration()

        then:
        beanInitResult.getDuration() >= 0
    }

    def "test getActualDuration"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
        beanInitResult.addChild(childBeanInitResult)
        // child finish first
        childBeanInitResult.duration()
        beanInitResult.duration()

        then:
        beanInitResult.getActualDuration() < 10
    }

    def "test duration"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
        beanInitResult.addChild(childBeanInitResult)
        // child finish first
        childBeanInitResult.duration()
        beanInitResult.duration()

        then:
        beanInitResult.getDuration() == beanInitResult.getActualDuration() + childBeanInitResult.getDuration()

    }

    def "test getTags"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        Map<String, String> tags = new HashMap<>()
        tags.put("class", "Test")
        beanInitResult.setTags(tags)

        then:
        beanInitResult.getTags().containsKey("class")

    }

    def "test getChildren"() {
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        assertTrue(beanInitResult.getChildren().isEmpty())
        BeanInitResult childBeanInitResult = new BeanInitResult("child")
        beanInitResult.addChild(childBeanInitResult)

        then:
        beanInitResult.getChildren().size() == 1

    }
}
