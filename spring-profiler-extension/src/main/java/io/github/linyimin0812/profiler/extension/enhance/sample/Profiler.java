package io.github.linyimin0812.profiler.extension.enhance.sample;

/**
 * @author yiminlin
 **/
public interface Profiler {

    String SAMPLE_THREAD_NAME_CONFIG_ID = "spring-startup-analyzer.async.profiler.sample.thread.names";
    String SAMPLE_INTERVAL_MILLIS_CONFIG_ID = "spring-startup-analyzer.async.profiler.interval.millis";

    void start();
    void stop();
}
