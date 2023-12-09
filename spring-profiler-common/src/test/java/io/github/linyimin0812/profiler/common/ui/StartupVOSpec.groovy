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
        when:
        BeanInitResult beanInitResult = new BeanInitResult("test")
        beanInitResult.duration()
        StartupVO.addBeanInitResult(beanInitResult)

        then:
        beanInitResult.getDuration() >= 0

    }

    def "test addStatistics"() {
        when:
        Statistics statistics = new Statistics(0, "Startup Time(s)", String.format("%.2f", 180.28))
        StartupVO.addStatistics(statistics)

        then:
        StartupVO.getStatisticsList().size() == 1
    }

    def "test addUnusedJar"() {
        when:
        URLClassLoader classLoader = new URLClassLoader(new URL[]{})
        Map<ClassLoader, Set<String>> unusedJarMap = new HashMap<>();
        Set<String> jars = new HashSet<>()
        jars.add("file:/Users/yiminlin/.m2/repository/org/webjars/swagger-ui/3.25.0/swagger-ui-3.25.0.jar")
        unusedJarMap.put(classLoader, jars)
        for (Map.Entry<ClassLoader, Set<String>> entry : unusedJarMap.entrySet()) {
            StartupVO.addUnusedJar(entry)
        }

        then:
        !unusedJarMap.isEmpty()
    }

    def "test addMethodInvokeDetail"() {
        when:
        MethodInvokeDetail invokeDetail = new MethodInvokeDetail("io.github.linyimin0812.profiler.common.ui.StartupVOTest.addMethodInvokeDetail", System.currentTimeMillis(), 10)
        StartupVO.addMethodInvokeDetail(invokeDetail)

        then:
        invokeDetail.getDuration() == 10
        StartupVO.getBeanInitResultList().size() == 1
        StartupVO.getStatisticsList().size() == 1
    }

    def "test getMethodInvokeDetailList"() {
        when:
        String text = StartupVO.toJSONString()
        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {})

        then:
        map.containsKey("methodInvokeDetailList")
    }

    def "test toJSONString"() {
        when:
        String text = StartupVO.toJSONString()
        Map<String, Object> map = JSONObject.parseObject(text, new TypeReference<Map<String, Object>>() {})

        then:
        map.containsKey("beanInitResultList")
        map.containsKey("statisticsList")
        map.containsKey("unusedJarMap")
        map.containsKey("methodInvokeDetailList")

    }
}
