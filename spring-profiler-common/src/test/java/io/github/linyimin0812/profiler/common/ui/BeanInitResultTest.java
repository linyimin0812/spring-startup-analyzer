package io.github.linyimin0812.profiler.common.ui;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
public class BeanInitResultTest {

    @Test
    public void getId() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        assertTrue(beanInitResult.getId() >= 1000);
    }

    @Test
    public void getParentId() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        BeanInitResult childBeanInitResult = new BeanInitResult("child");
        beanInitResult.addChild(childBeanInitResult);

        assertEquals(childBeanInitResult.getParentId(), beanInitResult.getId());
    }

    @Test
    public void getName() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        assertEquals("test", beanInitResult.getName());
    }

    @Test
    public void getDuration() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        beanInitResult.duration();
        assertTrue(beanInitResult.getDuration() >= 0);
    }

    @Test
    public void getActualDuration() {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        beanInitResult.addChild(childBeanInitResult);
        // child finish first
        childBeanInitResult.duration();

        beanInitResult.duration();

        assertTrue(beanInitResult.getActualDuration() < 10);

    }

    @Test
    public void duration() {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        beanInitResult.addChild(childBeanInitResult);
        // child finish first
        childBeanInitResult.duration();

        beanInitResult.duration();

        assertEquals(beanInitResult.getDuration(), beanInitResult.getActualDuration() + childBeanInitResult.getDuration());
    }

    @Test
    public void getTags() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        Map<String, String> tags = new HashMap<>();
        tags.put("class", "Test");
        beanInitResult.setTags(tags);

        assertTrue(beanInitResult.getTags().containsKey("class"));
    }

    @Test
    public void getChildren() {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        assertTrue(beanInitResult.getChildren().isEmpty());

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        beanInitResult.addChild(childBeanInitResult);

        assertEquals(1, beanInitResult.getChildren().size());
    }

    @Test
    public void addChild() {
        getChildren();
    }

    @Test
    public void setTags() {
        getTags();
    }
}