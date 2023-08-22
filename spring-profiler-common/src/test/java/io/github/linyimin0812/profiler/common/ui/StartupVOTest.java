package io.github.linyimin0812.profiler.common.ui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author linyimin
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StartupVOTest {

    @Test
    @Order(1)
    void addBeanInitResult() {
        BeanInitResult beanInitResult = new BeanInitResult("test");

        beanInitResult.duration();

        StartupVO.addBeanInitResult(beanInitResult);

        assertTrue(beanInitResult.getDuration() >= 0);

    }

    @Test
    @Order(1)
    void addStatistics() {
        Statistics statistics = new Statistics(0, "Startup Time(s)", String.format("%.2f", 180.28));
        StartupVO.addStatistics(statistics);
        assertEquals(1, StartupVO.getStatisticsList().size());
    }

    @Test
    @Order(1)
    void addUnusedJar() throws IOException {
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{})) {
            Map<ClassLoader, Set<String>> unusedJarMap = new HashMap<>();
            Set<String> jars = new HashSet<>();
            jars.add("file:/Users/yiminlin/.m2/repository/org/webjars/swagger-ui/3.25.0/swagger-ui-3.25.0.jar");
            unusedJarMap.put(classLoader, jars);
            for (Map.Entry<ClassLoader, Set<String>> entry : unusedJarMap.entrySet()) {
                StartupVO.addUnusedJar(entry);
            }
            assertFalse(unusedJarMap.isEmpty());
        }
    }

    @Test
    @Order(1)
    void addMethodInvokeDetail() {

        MethodInvokeDetail invokeDetail = new MethodInvokeDetail("io.github.linyimin0812.profiler.common.ui.StartupVOTest.addMethodInvokeDetail", System.currentTimeMillis(), 10);
        StartupVO.addMethodInvokeDetail(invokeDetail);

        assertEquals(10, invokeDetail.getDuration());

    }

    @Test
    @Order(2)
    void getBeanInitResultList() {
        assertEquals(1, StartupVO.getBeanInitResultList().size());
    }

    @Test
    @Order(2)
    void getStatisticsList() {
        assertEquals(1, StartupVO.getStatisticsList().size());
    }

    @Test
    @Order(2)
    void getMethodInvokeDetailList() {
        String text = StartupVO.toJSONString();

        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {});

        assertTrue(map.containsKey("methodInvokeDetailList"));
    }

    @Test
    @Order(2)
    void toJSONString() {

        String text = StartupVO.toJSONString();

        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {});

        assertTrue(map.containsKey("beanInitResultList"));
        assertTrue(map.containsKey("statisticsList"));
        assertTrue(map.containsKey("unusedJarMap"));
        assertTrue(map.containsKey("methodInvokeDetailList"));
    }
}