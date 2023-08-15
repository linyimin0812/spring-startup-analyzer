package io.github.linyimin0812.profiler.common.ui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class StartupVOTest {

    @BeforeClass
    public static void init() throws InterruptedException, IOException {

        BeanInitResult beanInitResult = new BeanInitResult("test");
        Thread.sleep(10);
        beanInitResult.duration();

        StartupVO.addBeanInitResult(beanInitResult);

        Statistics statistics = new Statistics(0, "Startup Time(s)", String.format("%.2f", 180.28));
        StartupVO.addStatistics(statistics);

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{})) {
            Map<ClassLoader, Set<String>> unusedJarMap = new HashMap<>();
            Set<String> jars = new HashSet<>();
            jars.add("file:/Users/yiminlin/.m2/repository/org/webjars/swagger-ui/3.25.0/swagger-ui-3.25.0.jar");
            unusedJarMap.put(classLoader, jars);
            for (Map.Entry<ClassLoader, Set<String>> entry : unusedJarMap.entrySet()) {
                StartupVO.addUnusedJar(entry);
            }
        }

        MethodInvokeDetail invokeDetail = new MethodInvokeDetail("io.github.linyimin0812.profiler.common.ui.StartupVOTest.addMethodInvokeDetail", System.currentTimeMillis(), 10);
        StartupVO.addMethodInvokeDetail(invokeDetail);
    }

    @Test
    public void addBeanInitResult() {
        assertEquals(1, StartupVO.getBeanInitResultList().size());
    }

    @Test
    public void addStatistics() {
        Assert.assertEquals(1, StartupVO.getStatisticsList().size());
    }

    @Test
    public void addUnusedJar() {

    }

    @Test
    public void addMethodInvokeDetail() {

    }

    @Test
    public void getBeanInitResultList() {
        assertEquals(1, StartupVO.getBeanInitResultList().size());
    }

    @Test
    public void getStatisticsList() {
        Assert.assertEquals(1, StartupVO.getStatisticsList().size());
    }

    @Test
    public void getMethodInvokeDetailList() {
    }

    @Test
    public void toJSONString() {

        String text = StartupVO.toJSONString();

        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {});

        Assert.assertTrue(map.containsKey("beanInitResultList"));
        Assert.assertTrue(map.containsKey("statisticsList"));
        Assert.assertTrue(map.containsKey("unusedJarMap"));
        Assert.assertTrue(map.containsKey("methodInvokeDetailList"));
    }
}