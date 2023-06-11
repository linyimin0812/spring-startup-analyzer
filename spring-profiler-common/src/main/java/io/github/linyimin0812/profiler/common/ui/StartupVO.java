package io.github.linyimin0812.profiler.common.ui;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyimin
 * @date 2023/06/11 16:27
 **/
public class StartupVO {

    private static final List<BeanInitResult> beanInitResultList = new ArrayList<>();
    private static final List<Statistics> statisticsList = new ArrayList<>();

    public static void addBeanInitResult(BeanInitResult beanInitResult) {
        beanInitResultList.add(beanInitResult);
    }

    public static void addStatistics(Statistics statistics) {
        statisticsList.add(statistics);
    }

    public static List<BeanInitResult> getBeanInitResultList() {
        return beanInitResultList;
    }

    public static List<Statistics> getStatisticsList() {
        return statisticsList;
    }

    public static String toJSONString() {
        Map<String, String> map = new HashMap<>();
        map.put("statisticsList", JSON.toJSONString(statisticsList));
        map.put("beanInitResultList", JSON.toJSONString(beanInitResultList));

        return JSON.toJSONString(map);
    }
}
