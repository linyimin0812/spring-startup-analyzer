package io.github.linyimin0812.profiler.extension.enhance.sample.jvmprofiler;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.NameUtil;
import io.github.linyimin0812.profiler.extension.enhance.sample.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
public class StacktraceProfiler implements Profiler {

    private final Logger logger = LogFactory.getStartupLogger();

    private List<Thread> sampledThreads = new ArrayList<>();

    private static final LinkedBlockingQueue<StackTraceElement[]> STACK_TRACE_QUEUE = new LinkedBlockingQueue<>();

    private static final Map<String, Integer> TRACE_MAP = new ConcurrentHashMap<>();
    private final ScheduledExecutorService SAMPLE_SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void start() {

        int interval = Integer.parseInt(ProfilerSettings.getProperty(SAMPLE_INTERVAL_MILLIS_CONFIG_ID, "10"));

        AtomicInteger count = new AtomicInteger();
        sampledThreads = getTargetThreads();

        SAMPLE_SCHEDULER.scheduleAtFixedRate(() -> {

            // refresh per second
            if (count.get() % (1000 / interval) == 0) {
                sampledThreads = getTargetThreads();
            }
            count.getAndIncrement();

            for (Thread thread : sampledThreads) {
                addStackTraceElements(thread.getStackTrace());
            }
        }, 0, interval, TimeUnit.MILLISECONDS);

        new Thread(new TraceProcessor()).start();
    }

    @Override
    public void stop() {

        SAMPLE_SCHEDULER.shutdown();
        TraceProcessor.stop();

        FlameGraph fg = new FlameGraph();
        try {
            fg.parse(NameUtil.getTemplatePath() + "flame-graph.html", NameUtil.getOutputPath() + NameUtil.getFlameGraphHtmlName(), TRACE_MAP);
        } catch (IOException e) {
            logger.error(StacktraceProfiler.class, "StacktraceProfiler stop error.", e);
        }

    }

    private synchronized void addStackTraceElements(StackTraceElement[] elements) {
        STACK_TRACE_QUEUE.add(elements);
    }

    static class TraceProcessor implements Runnable {

        private static boolean stop = false;

        @Override
        public void run() {
            while (true) {

                try {
                    StackTraceElement[] traces = STACK_TRACE_QUEUE.poll(5, TimeUnit.SECONDS);
                    if (traces == null || traces.length == 0) {
                        continue;
                    }
                    List<StackTraceElement> elements = Arrays.asList(traces);
                    Collections.reverse(elements);
                    String trace = elements.stream().map(element -> element.getClassName() + "." + element.getMethodName()).collect(Collectors.joining(";"));
                    TRACE_MAP.put(trace, TRACE_MAP.getOrDefault(trace, 0) + 1);
                } catch (InterruptedException ignored) {
                }

                if (stop && STACK_TRACE_QUEUE.isEmpty()) {
                    break;
                }
            }
        }

        public static void stop() {
            stop = true;
        }
    }

    private List<Thread> getTargetThreads() {

        String sampleThreadNames = ProfilerSettings.getProperty(SAMPLE_THREAD_NAME_CONFIG_ID);

        return new ArrayList<>(ThreadUtils.findThreads(thread -> {

            if (StringUtils.isBlank(sampleThreadNames)) {
                return true;
            }

            return Arrays.stream(sampleThreadNames.split(",")).anyMatch(name -> {
                if (name.contains("*")) {
                    return Pattern.compile(name).matcher(thread.getName()).matches();
                } else {
                    return name.equals(thread.getName());
                }
            });
        }));
    }
}
