package io.github.linyimin0812.profiler.extension.enhance.invoke;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetail;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class InvokeDetailListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private final Map<String /* processId_invokeId */, MethodInvokeDetail> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    private List<String> methodQualifiers = new ArrayList<>();

    @Override
    public boolean filter(String className) {
        return methodQualifiers.stream().anyMatch(qualifier -> qualifier.startsWith(className + "."));
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
            MethodInvokeDetail invokeDetail = new MethodInvokeDetail(buildMethodQualifier((AtEnterEvent) event), invokeEvent.args);
            INVOKE_DETAIL_MAP.put(key, invokeDetail);
        } else if (event instanceof AtExitEvent) {
            if (INVOKE_DETAIL_MAP.containsKey(key)) {
                MethodInvokeDetail invokeDetail = INVOKE_DETAIL_MAP.get(key);
                invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());
                StartupVO.addMethodInvokeDetail(invokeDetail);
            } else {
                logger.warn(InvokeDetailListener.class, "Key: {} does not exist in the Map, there may be an error.", key);
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
                ProfilerSettings.getProperty("spring-startup-analyzer.invoke.count.methods", "").split("\\|")
        ).map(StringUtils::trim).collect(Collectors.toList());

        logger.info(InvokeDetailListener.class, "config spring-startup-analyzer.invoke.count.methods is {}", ProfilerSettings.getProperty("spring-startup-analyzer.invoke.count.methods", ""));
    }

    @Override
    public void stop() {
        logger.info(InvokeDetailListener.class, "===============InvokeCountListener stop==================");
      
        methodQualifiers.clear();
        
        INVOKE_DETAIL_MAP.clear();
    }

    private String buildMethodQualifier(AtEnterEvent event) {

        String className = event.clazz.getSimpleName();

        return className + "." + event.methodName;
    }
}
