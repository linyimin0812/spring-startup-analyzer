package io.github.linyimin0812.profiler.common.jaeger;

import io.opentelemetry.api.internal.Utils;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author linyimin
 * @date 2023/04/26 10:46
 *
 **/
public class BatchSpanBasedDurationProcessorBuilder {

    static final long DEFAULT_SCHEDULE_DELAY_MILLIS = 5000L;
    static final int DEFAULT_MAX_QUEUE_SIZE = 2048;
    static final int DEFAULT_MAX_EXPORT_BATCH_SIZE = 512;
    static final int DEFAULT_EXPORT_TIMEOUT_MILLIS = 30000;
    static final int DEFAULT_MIN_SAMPLE_DURATION_MILLIS = 10;

    private final SpanExporter spanExporter;
    private long scheduleDelayNanos;
    private int maxQueueSize;
    private int maxExportBatchSize;
    private long exporterTimeoutNanos;
    private MeterProvider meterProvider;

    private long minSampleDurationMillis;

    BatchSpanBasedDurationProcessorBuilder(SpanExporter spanExporter) {
        this.scheduleDelayNanos = TimeUnit.MILLISECONDS.toNanos(DEFAULT_SCHEDULE_DELAY_MILLIS);
        this.maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;
        this.maxExportBatchSize = DEFAULT_MAX_EXPORT_BATCH_SIZE;
        this.exporterTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(DEFAULT_EXPORT_TIMEOUT_MILLIS);
        this.meterProvider = MeterProvider.noop();
        this.spanExporter = Objects.requireNonNull(spanExporter, "spanExporter");
        this.minSampleDurationMillis = DEFAULT_MIN_SAMPLE_DURATION_MILLIS;
    }

    public BatchSpanBasedDurationProcessorBuilder setScheduleDelay(long delay, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Utils.checkArgument(delay >= 0L, "delay must be non-negative");
        this.scheduleDelayNanos = unit.toNanos(delay);
        return this;
    }

    public BatchSpanBasedDurationProcessorBuilder setScheduleDelay(Duration delay) {
        Objects.requireNonNull(delay, "delay");
        return this.setScheduleDelay(delay.toNanos(), TimeUnit.NANOSECONDS);
    }


    public BatchSpanBasedDurationProcessorBuilder setExporterTimeout(long timeout, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Utils.checkArgument(timeout >= 0L, "timeout must be non-negative");
        this.exporterTimeoutNanos = unit.toNanos(timeout);
        return this;
    }

    public BatchSpanBasedDurationProcessorBuilder setExporterTimeout(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");
        return this.setExporterTimeout(timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    public BatchSpanBasedDurationProcessorBuilder setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
        return this;
    }

    public BatchSpanBasedDurationProcessorBuilder setMaxExportBatchSize(int maxExportBatchSize) {
        Utils.checkArgument(maxExportBatchSize > 0, "maxExportBatchSize must be positive.");
        this.maxExportBatchSize = maxExportBatchSize;
        return this;
    }

    public BatchSpanBasedDurationProcessorBuilder setMeterProvider(MeterProvider meterProvider) {
        Objects.requireNonNull(meterProvider, "meterProvider");
        this.meterProvider = meterProvider;
        return this;
    }

    public BatchSpanBasedDurationProcessorBuilder setMinSampleDurationMillis(long durationMillis) {
        this.minSampleDurationMillis = durationMillis;
        return this;
    }

    public BatchSpanBasedDurationProcessor build() {
        return new BatchSpanBasedDurationProcessor(this.spanExporter, this.meterProvider, this.scheduleDelayNanos, this.maxQueueSize, this.maxExportBatchSize, this.exporterTimeoutNanos, this.minSampleDurationMillis);
    }
}
