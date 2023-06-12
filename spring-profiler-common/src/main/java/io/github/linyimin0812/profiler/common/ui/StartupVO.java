package io.github.linyimin0812.profiler.common.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;
import java.util.stream.Collectors;

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

        map.put("methodInvokeDetailList", JSON.toJSONString(calculateInvokeMetrics(), SerializerFeature.IgnoreNonFieldGetter));

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

    private static List<MethodInvokeMetrics> calculateInvokeMetrics() {

        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        Map<String, List<MethodInvokeDetail>> invokeMap = methodInvokeDetailList.stream().collect(Collectors.groupingBy(MethodInvokeDetail::getMethodQualifier));

        for (Map.Entry<String, List<MethodInvokeDetail>> entry : invokeMap.entrySet()) {

            long totalCost = entry.getValue().stream().mapToLong(MethodInvokeDetail::getDuration).sum();
            double averageCost = totalCost / (1.0 * entry.getValue().size());
            List<MethodInvokeDetail> top100 = entry.getValue().stream().sorted((o1, o2) -> (int) (o2.getDuration() - o1.getDuration())).limit(100).collect(Collectors.toList());

            metricsList.add(new MethodInvokeMetrics(entry.getKey(), entry.getValue().size(), totalCost, averageCost, top100));

        }

        return metricsList;
    }
}
