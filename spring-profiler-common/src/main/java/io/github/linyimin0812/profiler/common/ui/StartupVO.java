package io.github.linyimin0812.profiler.common.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.utils.GsonUtil;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.TRANSIENT;

/**
 * @author linyimin
 **/
public class StartupVO {

    private static final Logger logger = LogFactory.getStartupLogger();

    private static final List<BeanInitResult> beanInitResultList = new ArrayList<>();
    private static final Set<String> beanNameSet = new HashSet<>();
    private static final List<Statistics> statisticsList = new ArrayList<>();
    private static final Map<String, Set<String>> unusedJarMap = new HashMap<>();
    private static final List<MethodInvokeDetail> methodInvokeDetailList = new ArrayList<>();

    private static final Gson GSON = GsonUtil.create();

    public static void addBeanInitResult(BeanInitResult beanInitResult) {
        beanInitResultList.add(beanInitResult);
    }
    
    public static boolean isNotContainThenAdd(String beanName){
        if(beanNameSet.contains(beanName)){
            return false;
        }else{
            beanNameSet.add(beanName);
            return true;
        }
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

    public static String toJSONString() {

        Map<String, String> map = new HashMap<>();

        map.put("statisticsList", GSON.toJson(statisticsList, new TypeToken<List<Statistics>>(){}.getType()));
        map.put("beanInitResultList", GSON.toJson(beanInitResultList));
        map.put("unusedJarMap", GSON.toJson(unusedJarMap));
        map.put("methodInvokeDetailList", GSON.toJson(calculateInvokeMetrics()));

        // fix Use JSONObject#toJSONString to serialize a Map. The Map contains a large string and OOM appears
        return GSON.toJson(map);
    }

    private static List<MethodInvokeMetrics> calculateInvokeMetrics() {

        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        try {
            Map<String, List<MethodInvokeDetail>> invokeMap = methodInvokeDetailList.stream().collect(Collectors.groupingBy(MethodInvokeDetail::getMethodQualifier));

            for (Map.Entry<String, List<MethodInvokeDetail>> entry : invokeMap.entrySet()) {

                long totalCost = entry.getValue().stream().mapToLong(MethodInvokeDetail::getDuration).sum();
                double averageCost = totalCost / (1.0 * entry.getValue().size());
                List<MethodInvokeDetail> top100 = entry.getValue().stream().sorted((o1, o2) -> (int) (o2.getDuration() - o1.getDuration())).limit(100).collect(Collectors.toList());

                metricsList.add(new MethodInvokeMetrics(entry.getKey(), entry.getValue().size(), totalCost, averageCost, top100));

            }
        } catch (Exception ex) {
            List<MethodInvokeDetail> copies = methodInvokeDetailList.stream()
                    .filter(Objects::nonNull)
                    .map(invokeDetail -> new MethodInvokeDetail(invokeDetail.getMethodQualifier(), invokeDetail.getStartMillis(), invokeDetail.getDuration()))
                    .collect(Collectors.toList());
            logger.error(StartupVO.class, "calculateInvokeMetrics error. methodInvokeDetailList: {}", GSON.toJson(copies), ex);
        }

        return metricsList;
    }
}
