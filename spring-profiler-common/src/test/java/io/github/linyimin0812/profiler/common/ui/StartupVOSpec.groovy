package io.github.linyimin0812.profiler.common.ui

import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author linyimin
 * */
@Stepwise
class StartupVOSpec extends Specification {

    def "test addBeanInitResult"() {

        given:
        BeanInitResult beanInitResult = new BeanInitResult("test")

        when:
        beanInitResult.duration()
        StartupVO.addBeanInitResult(beanInitResult)

        then:
        beanInitResult.getDuration() >= 0

    }

    def "test addStatistics"() {

        given:
        Statistics statistics = new Statistics(0, "Startup Time(s)", String.format("%.2f", 180.28))

        when:
        StartupVO.addStatistics(statistics)

        then:
        StartupVO.getStatisticsList().size() == 1
    }

    def "test addUnusedJar"() {

        given:
        URLClassLoader classLoader = new URLClassLoader(new URL[]{})
        Map<ClassLoader, Set<String>> unusedJarMap = new HashMap<>();
        Set<String> jars = new HashSet<>()

        when:
        jars.add("file:/Users/yiminlin/.m2/repository/org/webjars/swagger-ui/3.25.0/swagger-ui-3.25.0.jar")
        unusedJarMap.put(classLoader, jars)
        for (Map.Entry<ClassLoader, Set<String>> entry : unusedJarMap.entrySet()) {
            StartupVO.addUnusedJar(entry)
        }

        then:
        !unusedJarMap.isEmpty()
    }

    def "test addMethodInvokeDetail"() {

        given:
        MethodInvokeDetail invokeDetail = new MethodInvokeDetail("io.github.linyimin0812.profiler.common.ui.StartupVOTest.addMethodInvokeDetail", System.currentTimeMillis(), 10)

        when:
        StartupVO.addMethodInvokeDetail(invokeDetail)

        then:
        invokeDetail.getDuration() == 10
        StartupVO.getBeanInitResultList().size() == 1
        StartupVO.getStatisticsList().size() == 1
    }

    def "test getMethodInvokeDetailList"() {

        given:
        String text = StartupVO.toJSONString()

        when:
        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {})

        then:
        map.containsKey("methodInvokeDetailList")
    }

    def "test toJSONString"() {

        given:
        String text = StartupVO.toJSONString()

        when:
        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {})

        then:
        map.containsKey("beanInitResultList")
        map.containsKey("statisticsList")
        map.containsKey("unusedJarMap")
        map.containsKey("methodInvokeDetailList")

    }
}
