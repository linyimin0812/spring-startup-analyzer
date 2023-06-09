package io.github.linyimin0812.profiler.common.jaeger;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.internal.DaemonThreadFactory;
import io.opentelemetry.sdk.internal.ThrowableUtil;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.internal.JcTools;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author linyimin
 * @date 2023/04/26 10:43
 * @description
 **/
public class BatchSpanBasedDurationProcessor implements SpanProcessor {

    private static final Logger logger = LogFactory.getStartupLogger();
    private static final String WORKER_THREAD_NAME = BatchSpanProcessor.class.getSimpleName() + "_WorkerThread";
    private static final AttributeKey<String> SPAN_PROCESSOR_TYPE_LABEL = AttributeKey.stringKey("spanProcessorType");
    private static final AttributeKey<Boolean> SPAN_PROCESSOR_DROPPED_LABEL = AttributeKey.booleanKey("dropped");
    private static final String SPAN_PROCESSOR_TYPE_VALUE = BatchSpanProcessor.class.getSimpleName();
    private final BatchSpanBasedDurationProcessor.Worker worker;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    private final long minSampleDurationMillis;

    public static BatchSpanBasedDurationProcessorBuilder builder(SpanExporter spanExporter) {
        return new BatchSpanBasedDurationProcessorBuilder(spanExporter);
    }

    BatchSpanBasedDurationProcessor(SpanExporter spanExporter, MeterProvider meterProvider, long scheduleDelayNanos, int maxQueueSize, int maxExportBatchSize, long exporterTimeoutNanos, long minSampleDurationMillis) {
        this.worker = new BatchSpanBasedDurationProcessor.Worker(spanExporter, meterProvider, scheduleDelayNanos, maxExportBatchSize, exporterTimeoutNanos, JcTools.newFixedSizeQueue(maxQueueSize));
        Thread workerThread = (new DaemonThreadFactory(WORKER_THREAD_NAME)).newThread(this.worker);
        this.minSampleDurationMillis = minSampleDurationMillis;
        workerThread.start();
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
    }

    @Override
    public boolean isStartRequired() {
        return false;
    }

    @Override
    public void onEnd(ReadableSpan span) {
        if (span.getSpanContext().isSampled() && isSampleBasedDuration(span)) {
            this.worker.addSpan(span);
        }
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }

    @Override
    public CompletableResultCode shutdown() {
        return this.isShutdown.getAndSet(true) ? CompletableResultCode.ofSuccess() : this.worker.shutdown();
    }

    @Override
    public CompletableResultCode forceFlush() {
        return this.worker.forceFlush();
    }

    @Override
    public String toString() {
        return "BatchSpanProcessor{spanExporter=" + this.worker.spanExporter + ", scheduleDelayNanos=" + this.worker.scheduleDelayNanos + ", maxExportBatchSize=" + this.worker.maxExportBatchSize + ", exporterTimeoutNanos=" + this.worker.exporterTimeoutNanos + '}';
    }

    private boolean isSampleBasedDuration(ReadableSpan span) {
        return TimeUnit.NANOSECONDS.toMillis(span.getLatencyNanos()) >= minSampleDurationMillis;
    }

    private static final class Worker implements Runnable {
        private final LongCounter processedSpansCounter;
        private final Attributes droppedAttrs;
        private final Attributes exportedAttrs;
        private final SpanExporter spanExporter;
        private final long scheduleDelayNanos;
        private final int maxExportBatchSize;
        private final long exporterTimeoutNanos;
        private long nextExportTime;
        private final Queue<ReadableSpan> queue;
        private final AtomicInteger spansNeeded;
        private final BlockingQueue<Boolean> signal;
        private final AtomicReference<CompletableResultCode> flushRequested;
        private volatile boolean continueWork;
        private final ArrayList<SpanData> batch;

        private Worker(SpanExporter spanExporter, MeterProvider meterProvider, long scheduleDelayNanos, int maxExportBatchSize, long exporterTimeoutNanos, Queue<ReadableSpan> queue) {
            this.spansNeeded = new AtomicInteger(Integer.MAX_VALUE);
            this.flushRequested = new AtomicReference<>();
            this.continueWork = true;
            this.spanExporter = spanExporter;
            this.scheduleDelayNanos = scheduleDelayNanos;
            this.maxExportBatchSize = maxExportBatchSize;
            this.exporterTimeoutNanos = exporterTimeoutNanos;
            this.queue = queue;
            this.signal = new ArrayBlockingQueue<>(1);
            Meter meter = meterProvider.meterBuilder("io.opentelemetry.sdk.trace").build();
            meter.gaugeBuilder("queueSize")
                    .ofLongs()
                    .setDescription("The number of spans queued")
                    .setUnit("1")
                    .buildWithCallback((result) -> result.record(
                            queue.size(),
                            Attributes.of(SPAN_PROCESSOR_TYPE_LABEL, SPAN_PROCESSOR_TYPE_VALUE))
                    );
            this.processedSpansCounter = meter.counterBuilder("processedSpans").setUnit("1").setDescription("The number of spans processed by the BatchSpanProcessor. [dropped=true if they were dropped due to high throughput]").build();
            this.droppedAttrs = Attributes.of(BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_TYPE_LABEL, BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_TYPE_VALUE, BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_DROPPED_LABEL, true);
            this.exportedAttrs = Attributes.of(BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_TYPE_LABEL, BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_TYPE_VALUE, BatchSpanBasedDurationProcessor.SPAN_PROCESSOR_DROPPED_LABEL, false);
            this.batch = new ArrayList<>(this.maxExportBatchSize);
        }

        private void addSpan(ReadableSpan span) {
            if (!this.queue.offer(span)) {
                this.processedSpansCounter.add(1L, this.droppedAttrs);
            } else if (this.queue.size() >= this.spansNeeded.get() && !this.signal.offer(true)) {
                logger.warn("signal offer failed.");
            }

        }

        public void run() {
            this.updateNextExportTime();

            while(this.continueWork) {
                if (this.flushRequested.get() != null) {
                    this.flush();
                }

                JcTools.drain(this.queue, this.maxExportBatchSize - this.batch.size(), (span) -> this.batch.add(span.toSpanData()));
                if (this.batch.size() >= this.maxExportBatchSize || System.nanoTime() >= this.nextExportTime) {
                    this.exportCurrentBatch();
                    this.updateNextExportTime();
                }

                if (this.queue.isEmpty()) {
                    try {
                        long pollWaitTime = this.nextExportTime - System.nanoTime();
                        if (pollWaitTime > 0L) {
                            this.spansNeeded.set(this.maxExportBatchSize - this.batch.size());
                            this.signal.poll(pollWaitTime, TimeUnit.NANOSECONDS);
                            this.spansNeeded.set(Integer.MAX_VALUE);
                        }
                    } catch (InterruptedException var3) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

        }

        private void flush() {
            int spansToFlush = this.queue.size();

            while(spansToFlush > 0) {
                ReadableSpan span = this.queue.poll();

                assert span != null;

                this.batch.add(span.toSpanData());
                --spansToFlush;
                if (this.batch.size() >= this.maxExportBatchSize) {
                    this.exportCurrentBatch();
                }
            }

            this.exportCurrentBatch();
            CompletableResultCode flushResult = this.flushRequested.get();
            if (flushResult != null) {
                flushResult.succeed();
                this.flushRequested.set(null);
            }

        }

        private void updateNextExportTime() {
            this.nextExportTime = System.nanoTime() + this.scheduleDelayNanos;
        }

        private CompletableResultCode shutdown() {
            CompletableResultCode result = new CompletableResultCode();
            CompletableResultCode flushResult = this.forceFlush();
            flushResult.whenComplete(() -> {
                this.continueWork = false;
                CompletableResultCode shutdownResult = this.spanExporter.shutdown();
                shutdownResult.whenComplete(() -> {
                    if (flushResult.isSuccess() && shutdownResult.isSuccess()) {
                        result.succeed();
                    } else {
                        result.fail();
                    }

                });
            });
            return result;
        }

        private CompletableResultCode forceFlush() {
            CompletableResultCode flushResult = new CompletableResultCode();
            if (this.flushRequested.compareAndSet(null, flushResult) && !this.signal.offer(true)) {
                logger.warn("signal offer failed.");
            }

            CompletableResultCode possibleResult = this.flushRequested.get();
            return possibleResult == null ? CompletableResultCode.ofSuccess() : possibleResult;
        }

        private void exportCurrentBatch() {
            if (!this.batch.isEmpty()) {
                try {
                    CompletableResultCode result = this.spanExporter.export(Collections.unmodifiableList(this.batch));
                    result.join(this.exporterTimeoutNanos, TimeUnit.NANOSECONDS);
                    if (result.isSuccess()) {
                        this.processedSpansCounter.add(this.batch.size(), this.exportedAttrs);
                    } else {
                        BatchSpanBasedDurationProcessor.logger.error("Exporter failed");
                    }
                } catch (Exception e) {
                    ThrowableUtil.propagateIfFatal(e);
                    BatchSpanBasedDurationProcessor.logger.warn("Exporter threw an Exception", e);
                } finally {
                    this.batch.clear();
                }

            }
        }
    }
}
