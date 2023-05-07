package com.github.linyimin.profiler.extension.enhance.springbean;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.api.EventListener;
import com.github.linyimin.profiler.api.event.AtEnterEvent;
import com.github.linyimin.profiler.api.event.AtExitEvent;
import com.github.linyimin.profiler.api.event.Event;
import com.github.linyimin.profiler.api.event.InvokeEvent;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.markdown.MarkdownStatistics;
import com.github.linyimin.profiler.common.markdown.MarkdownWriter;
import com.github.linyimin.profiler.common.settings.ProfilerSettings;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.MetaInfServices;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author linyimin
 * @date 2023/05/07 14:46
 **/
@MetaInfServices(EventListener.class)
public class BeanInitializeListener extends BeanListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private final Map<String/* processId_invokeId */, BeanInitializeResult> RESULT_MAP = new ConcurrentHashMap<>();

    @Override
    public void onEvent(Event event) {
        InvokeEvent invokeEvent = (InvokeEvent) event;

        String key = String.format("%s_%s", invokeEvent.processId, invokeEvent.invokeId);

        if (invokeEvent.args == null || invokeEvent.args.length < 3) {
            int size = invokeEvent.args == null ? 0 : invokeEvent.args.length;
            logger.warn("{} need 3 arguments, but only {} arguments.", getMethodName(), size);
            return;
        }
        String beanName = (String) invokeEvent.args[0];
        String className = invokeEvent.args[1].getClass().getName();

        if (event instanceof AtEnterEvent) {
            RESULT_MAP.put(key, new BeanInitializeResult(beanName, className));
        } else if (event instanceof AtExitEvent) {
            if (!RESULT_MAP.containsKey(key)) {
                logger.warn("key {} is not exist. beanName: {}, className: {}", key, beanName, className);
                return;
            }
            RESULT_MAP.get(key).endMillis = System.currentTimeMillis();
        }

    }

    @Override
    public void start() {
        logger.info("============BeanInitializeListener start=============");
    }

    @Override
    public void stop() {
        logger.info("============BeanInitializeListener stop=============");

        if (RESULT_MAP.isEmpty()) {
            return;
        }

        // Num of Beans
        reportNumOfBeans();

        // Num of Beans Take Longer Time
        reportNumOfBeanTakeLongerTime();

        // Detail of Beans Take Longer Time
        reportDetailOfBeanTakeLongerTime();

        RESULT_MAP.clear();
    }

    private void reportNumOfBeans() {
        MarkdownStatistics.write("Num of Beans", String.valueOf(RESULT_MAP.values().size()));
    }

    private void reportNumOfBeanTakeLongerTime() {

        long minCost = Long.parseLong(ProfilerSettings.getProperty("java-profiler.spring.bean.init.min.millis"));

        long count = RESULT_MAP.values().stream()
                .filter(result -> result.endMillis > 0)
                .filter(result -> (result.endMillis - result.startMillis) >= minCost)
                .count();

        MarkdownStatistics.write(String.format("Num of Beans(>= %s ms)", minCost), String.valueOf(count));
    }

    private void reportDetailOfBeanTakeLongerTime() {

        long minCost = Long.parseLong(ProfilerSettings.getProperty("java-profiler.spring.bean.init.min.millis"));

        List<BeanInitializeResult> list = RESULT_MAP.values().stream()
                .filter(result -> result.endMillis > 0)
                .filter(result -> (result.endMillis - result.startMillis) >= minCost)
                .sorted(BeanInitializeResult::compareTo)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return;
        }

        StringBuilder topBeanBuilder = new StringBuilder("<details open>\n")
                .append(String.format("<summary><h1 style='display: inline'>Details of Bean(>= %s ms)</h1></summary>\n", minCost))
                .append("<hr/>\n")
                .append("<table>\n")
                .append("<tr>\n")
                .append(String.format("<th>%s</th>\n", "Bean Name"))
                .append(String.format("<th>%s</th>\n", "Cost(ms)"))
                .append(String.format("<th>%s</th>\n", "Class Name"))
                .append("</tr>\n")
                ;
        for (BeanInitializeResult result : list) {
            topBeanBuilder.append("<tr>\n")
                    .append(String.format("<td>%s</td>\n", result.beanName))
                    .append(String.format("<td style='text-align: center;'>%s</td>\n", result.endMillis - result.startMillis))
                    .append(String.format("<td>%s</td>\n", result.className))
                    .append("</tr>\n");
        }

        topBeanBuilder.append("</table>\n").append("</details>\n\n").append("<hr/>\n");

        MarkdownWriter.write(1, topBeanBuilder.toString());

    }

    @Override
    public String getMethodName() {
        return "initializeBean";
    }

    @Override
    public String[] getMethodTypes() {
        return new String[] {
                "java.lang.String",
                "java.lang.Object",
                "org.springframework.beans.factory.support.RootBeanDefinition"
        };
    }

    private static class BeanInitializeResult implements Comparable<BeanInitializeResult> {
        public String beanName;
        public String className;
        public long startMillis;
        public long endMillis;

        public BeanInitializeResult(String beanName, String className) {
            this.beanName = beanName;
            this.className = className;
            this.startMillis = System.currentTimeMillis();
        }

        @Override
        public int compareTo(@NotNull BeanInitializeResult o) {
            return (this.endMillis - this.startMillis) > (o.endMillis - o.startMillis) ? -1 : 1;
        }
    }
}
