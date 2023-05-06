package com.github.linyimin.profiler.extension.enhance.invoke;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.api.EventListener;
import com.github.linyimin.profiler.api.event.AtEnterEvent;
import com.github.linyimin.profiler.api.event.AtExitEvent;
import com.github.linyimin.profiler.api.event.Event;
import com.github.linyimin.profiler.api.event.InvokeEvent;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.markdown.MarkdownWriter;
import com.github.linyimin.profiler.common.settings.ProfilerSettings;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author linyimin
 * @date 2023/05/03 17:52
 * @description
 **/
@MetaInfServices(EventListener.class)
public class InvokeDetailListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private final Map<String /* processId_invokeId */, InvokeDetail> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    private List<String> methodQualifiers = new ArrayList<>();

    @Override
    public boolean filter(String className) {
        return methodQualifiers.stream().anyMatch(qualifier -> qualifier.startsWith(className));
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {

        String methodQualifier;

        if (methodTypes == null || methodTypes.length == 0) {
            methodQualifier = methodName + "()";
        } else {
            methodQualifier = methodName + "(" + String.join(",", methodTypes) + ")";
        }

        return methodQualifiers.stream().anyMatch(qualifier -> qualifier.endsWith(methodQualifier));
    }

    @Override
    public void onEvent(Event event) {

        InvokeEvent invokeEvent = (InvokeEvent) event;

        String key = String.format("%s_%s", invokeEvent.processId, invokeEvent.invokeId);

        if (event instanceof AtEnterEvent) {
            InvokeDetail invokeDetail = new InvokeDetail(buildMethodQualifier((AtEnterEvent) event), invokeEvent.args);
            INVOKE_DETAIL_MAP.put(key, invokeDetail);
        } else if (event instanceof AtExitEvent) {
            if (INVOKE_DETAIL_MAP.containsKey(key)) {
                InvokeDetail invokeDetail = INVOKE_DETAIL_MAP.get(key);
                invokeDetail.endMillis = System.currentTimeMillis();
            } else {
                logger.warn("Key: {} does not exist in the Map, there may be an error.", key);
            }
        }
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {

        INVOKE_DETAIL_MAP.clear();

        methodQualifiers = Arrays.stream(
                ProfilerSettings.getProperty("java-profiler.invoke.count.methods", "").split("\\|")
        ).map(StringUtils::trim).collect(Collectors.toList());

    }

    @Override
    public void stop() {
        logger.info("===============InvokeCountListener stop==================");

        StringBuilder invokeDetailTable = new StringBuilder()
                .append("# Invoke Details\n")
                .append("---\n")
                .append("<table>\n")
                .append("<tr>\n")
                .append("<th>Method</th>\n")
                .append("<th>Invoke Count</th>\n")
                .append("<th>Total Cost(ms)</th>\n")
                .append("<th>Average Cost(ms)</th>\n")
                .append("</tr>");

        Map<String, List<InvokeDetail>> map = INVOKE_DETAIL_MAP.values().stream().collect(Collectors.groupingBy(detail -> detail.methodQualifier));

        for (List<InvokeDetail> list : map.values()) {

            List<InvokeDetail> invokeDetails = list.stream().filter(detail -> detail.endMillis > 0).collect(Collectors.toList());

            int count = invokeDetails.size();
            long totalCost = invokeDetails.stream().mapToLong(detail -> detail.endMillis - detail.startMillis).sum();
            double averageCost = totalCost / (count * 1D);

            invokeDetailTable.append("<tr>")
                    .append(String.format("<td>%s</td>\n", buildTopCostInvokeInfo(invokeDetails)))
                    .append(String.format("<td style='text-align: center;'>%s</td>\n", count))
                    .append(String.format("<td style='text-align: center;'>%s</td>\n", totalCost))
                    .append(String.format("<td style='text-align: center;'>%s</td>\n", String.format("%.2f", averageCost)))
                    .append("</tr>\n");
        }

        invokeDetailTable.append("</table>\n");

        MarkdownWriter.write(invokeDetailTable.toString());

        INVOKE_DETAIL_MAP.clear();
    }

    private String buildTopCostInvokeInfo(List<InvokeDetail> details) {

        details.sort(((o1, o2) -> (int) ((o2.endMillis - o2.startMillis) - (o1.endMillis - o1.startMillis))));
        List<InvokeDetail> invokeDetails = details.subList(0, Math.min(details.size(), 5));

        String methodName = invokeDetails.get(0).methodQualifier;

        StringBuilder topDetails = new StringBuilder("<details>\n")
                .append(String.format("<Summary>%s</summary>", methodName))
                .append("<table>\n")
                .append("<tr>\n")
                .append("<th>Arguments</th>\n")
                .append("<th>Invoke Cost(ms)</th>\n")
                .append("</tr>\n");

        for (InvokeDetail detail : invokeDetails) {
            topDetails.append("<tr>\n");

            if (detail.args == null || detail.args.length == 0) {
                topDetails.append("<td>-</td>\n");
            } else {
                topDetails.append("<td>\n").append("<ul>\n");
                for (Object obj : detail.args) {
                    topDetails.append(String.format("<li>%s</li>\n", obj));
                }
                topDetails.append("</ul>\n").append("</td>\n");
            }

            topDetails.append(String.format("<td style='text-align: center;'>%s</td>\n", detail.endMillis - detail.startMillis));

            topDetails.append("</tr>\n");
        }

        topDetails.append("</table>\n");

        return topDetails.toString();

    }

    private String buildMethodQualifier(AtEnterEvent event) {

        String className = event.clazz.getSimpleName();

        return className + "." + event.methodName;
    }

    private static class InvokeDetail {
        public String methodQualifier;
        public long startMillis;
        public long endMillis;

        public Object[] args;

        public InvokeDetail(String methodQualifier, Object[] args) {
            this.methodQualifier = methodQualifier;
            this.startMillis = System.currentTimeMillis();
            this.args = args;
        }
    }
}
