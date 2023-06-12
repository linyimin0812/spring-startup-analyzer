package io.github.linyimin0812.profiler.common.ui;

import com.alibaba.fastjson2.JSON;

import java.util.*;

/**
 * @author linyimin
 * @date 2023/06/11 16:27
 **/
public class StartupVO {

    private static final List<BeanInitResult> beanInitResultList = new ArrayList<>();
    private static final List<Statistics> statisticsList = new ArrayList<>();
    private static final Map<String, Set<String>> unusedJarMap = new HashMap<>();
    private static final List<MethodInvokeDetail> methodInvokeDetailList = new ArrayList<>();

    public static void addBeanInitResult(BeanInitResult beanInitResult) {
        beanInitResultList.add(beanInitResult);
    }

    public static void addStatistics(Statistics statistics) {
        statisticsList.add(statistics);
    }

    public static void addUnusedJar(Map.Entry<ClassLoader, Set<String>> entry) {
        unusedJarMap.put(entry.getKey().toString(), entry.getValue());
    }

    public static void addMethodInvokeDetail(MethodInvokeDetail invokeDetail) {
        methodInvokeDetailList.add(invokeDetail);
    }

    public static List<BeanInitResult> getBeanInitResultList() {
        return beanInitResultList;
    }

    public static List<Statistics> getStatisticsList() {
        return statisticsList;
    }

    public static List<MethodInvokeDetail> getMethodInvokeDetailList() {
        return methodInvokeDetailList;
    }

    public static String toJSONString() {
        Map<String, String> map = new HashMap<>();
        map.put("statisticsList", JSON.toJSONString(statisticsList));
        map.put("beanInitResultList", JSON.toJSONString(beanInitResultList));
        map.put("unusedJarMap", JSON.toJSONString(unusedJarMap));
        map.put("methodInvokeDetailList", JSON.toJSONString(methodInvokeDetailList));

        return JSON.toJSONString(map);
    }

    public static void clearBeanInitResultList() {
        beanInitResultList.clear();
    }

    public static void clearStatisticsList() {
        statisticsList.clear();
    }

    public static void clearUnusedJarMap() {
        unusedJarMap.clear();
    }

    public static void clearMethodInvokeDetailList() {
        methodInvokeDetailList.clear();
    }
}
