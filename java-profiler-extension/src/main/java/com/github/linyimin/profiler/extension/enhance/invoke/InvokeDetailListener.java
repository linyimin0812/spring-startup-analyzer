package com.github.linyimin.profiler.extension.enhance.invoke;

import ch.qos.logback.classic.Logger;
import com.alibaba.deps.org.objectweb.asm.Type;
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
            InvokeDetail invokeDetail = new InvokeDetail(buildMethodQualifier((AtEnterEvent) event));
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
                .append("\n")
                .append("| Method | Invoke Count | Total Cost(ms) | Average Cost(ms) |\n")
                .append("| --- | :---: | :---: | :---: |\n");

        Map<String, List<InvokeDetail>> map = INVOKE_DETAIL_MAP.values().stream().collect(Collectors.groupingBy(detail -> detail.methodQualifier));

        for (List<InvokeDetail> list : map.values()) {

            String methodQualifier = list.get(0).methodQualifier;

            List<InvokeDetail> invokeDetails = list.stream().filter(detail -> detail.endMillis > 0).collect(Collectors.toList());

            int count = invokeDetails.size();
            long totalCost = invokeDetails.stream().mapToLong(detail -> detail.endMillis - detail.startMillis).sum();
            double averageCost = totalCost / (count * 1D);

            invokeDetailTable.append("| ")
                    .append(methodQualifier).append(" | ")
                    .append(count).append(" | ")
                    .append(totalCost).append(" | ")
                    .append(String.format("%.2f", averageCost)).append(" |\n");
        }

        MarkdownWriter.write(invokeDetailTable.toString());

        INVOKE_DETAIL_MAP.clear();
    }

    private String buildMethodQualifier(AtEnterEvent event) {

        String className = event.clazz.getName();

        Type methodType = Type.getMethodType(event.methodDesc);
        Type[] types = methodType.getArgumentTypes();
        if (types.length == 0) {
            return className + "." + event.methodName + "()";
        }

        String args = Arrays.stream(types).map(Type::getClassName).collect(Collectors.joining(","));

        return className + "." + event.methodName + "(" + args + ")";
    }

    private static class InvokeDetail {
        public String methodQualifier;
        public long startMillis;
        public long endMillis;

        public InvokeDetail(String methodQualifier) {
            this.methodQualifier = methodQualifier;
            this.startMillis = System.currentTimeMillis();
        }
    }
}
