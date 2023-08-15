package io.github.linyimin0812.profiler.common.ui;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linyimin
 **/
public class BeanInitResultTest {

    @Test
    public void getId() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        Assert.assertTrue(beanInitResult.getId() >= 1000);
    }

    @Test
    public void getParentId() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        BeanInitResult childBeanInitResult = new BeanInitResult("child");
        beanInitResult.addChild(childBeanInitResult);

        Assert.assertEquals(childBeanInitResult.getParentId(), beanInitResult.getId());
    }

    @Test
    public void getName() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        Assert.assertEquals("test", beanInitResult.getName());
    }

    @Test
    public void getDuration() throws InterruptedException {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        Thread.sleep(10);
        beanInitResult.duration();
        Assert.assertTrue(beanInitResult.getDuration() >= 10);
    }

    @Test
    public void getActualDuration() throws InterruptedException {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        Thread.sleep(10);

        beanInitResult.addChild(childBeanInitResult);
        // child finish first
        childBeanInitResult.duration();

        beanInitResult.duration();

        Assert.assertTrue(beanInitResult.getActualDuration() < 10);

    }

    @Test
    public void duration() throws InterruptedException {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        Thread.sleep(10);

        beanInitResult.addChild(childBeanInitResult);
        // child finish first
        childBeanInitResult.duration();

        beanInitResult.duration();

        Assert.assertEquals(beanInitResult.getDuration(), beanInitResult.getActualDuration() + childBeanInitResult.getDuration());
    }

    @Test
    public void getTags() {
        BeanInitResult beanInitResult = new BeanInitResult("test");
        Map<String, String> tags = new HashMap<>();
        tags.put("class", "Test");
        beanInitResult.setTags(tags);

        Assert.assertTrue(beanInitResult.getTags().containsKey("class"));
    }

    @Test
    public void getChildren() {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        Assert.assertTrue(beanInitResult.getChildren().isEmpty());

        BeanInitResult childBeanInitResult = new BeanInitResult("child");

        beanInitResult.addChild(childBeanInitResult);

        Assert.assertEquals(1, beanInitResult.getChildren().size());
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