package io.github.linyimin0812.profiler.common.jaeger;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.AppNameUtil;
import io.github.linyimin0812.profiler.common.utils.IpUtil;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.picocontainer.Startable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author linyimin
 * @date 2023/04/21 11:11
 * @description jaeger初始化
 **/
public class Jaeger implements Startable {

    private SdkTracerProvider provider;

    private static String serviceName;

    @Override
    public void start() {

        String grpcEndpoint = ProfilerSettings.getProperty("spring-startup-analyzer.jaeger.grpc.export.endpoint");

        SpanExporter exporter = JaegerGrpcSpanExporter.builder().setEndpoint(grpcEndpoint).build();

        Resource resource = Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, getServiceName())));

        long minSampleDuration = Long.parseLong(ProfilerSettings.getProperty("spring-startup-analyzer.jaeger.span.min.sample.duration.millis", "10"));

        SpanProcessor spanProcessor = BatchSpanBasedDurationProcessor
                .builder(exporter)
                .setMinSampleDurationMillis(minSampleDuration)
                .build();

        provider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setSampler(Sampler.alwaysOn())
                .setResource(resource)
                .build();
    }

    public Tracer createTracer(String name) {
        return provider.get(name);
    }

    @Override
    public void stop() {
        provider.close();
    }

    public static String getServiceName() {

        if (serviceName == null) {
            String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            serviceName = String.format("%s-%s-%s", AppNameUtil.getAppName(), currentTime, IpUtil.getIp());
        }

        return serviceName;
    }
}
