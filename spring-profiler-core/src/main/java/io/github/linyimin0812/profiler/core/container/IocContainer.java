package io.github.linyimin0812.profiler.core.container;

import io.github.linyimin0812.profiler.common.file.FileProcessor;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.markdown.MarkdownStatistics;
import io.github.linyimin0812.profiler.common.markdown.MarkdownWriter;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.core.http.SimpleHttpServer;
import io.github.linyimin0812.profiler.core.monitor.StartupMonitor;
import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.Lifecycle;
import io.github.linyimin0812.profiler.core.monitor.check.AppStatusCheckService;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.slf4j.Logger;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author linyimin
 * @date 2023/04/13 11:45
 * @description 生命周期实例容器，由容器统一控制
 **/
public class IocContainer {


    private static final Logger startupLogger = LogFactory.getStartupLogger();

    private static final MutablePicoContainer container = new PicoBuilder().withSetterInjection().withCaching().withLifecycle().build();

    private static final AtomicBoolean started = new AtomicBoolean();

    private static final AtomicBoolean stopped = new AtomicBoolean();

    private static long startTimeMillis = 0;

    /**
     * 启动服务容器
     */
    public static void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        container.start();

        for (Lifecycle lifecycle : ServiceLoader.load(Lifecycle.class, IocContainer.class.getClassLoader())) {
            container.addComponent(lifecycle);
        }

        for (EventListener listener : ServiceLoader.load(EventListener.class, IocContainer.class.getClassLoader())) {
            container.addComponent(listener);
        }

        for (AppStatusCheckService service : ServiceLoader.load(AppStatusCheckService.class, StartupMonitor.class.getClassLoader())) {
            service.init();
            container.addComponent(service);
        }

        SimpleHttpServer.start();

        startTimeMillis = System.currentTimeMillis();

    }

    /**
     * 停止容器
     */
    public static void stop() {

        if (!stopped.compareAndSet(false, true)) {
            startupLogger.warn("IocContainer has stopped.");
            return;
        }

        double startupDuration = (System.currentTimeMillis() - startTimeMillis) / 1000D;

        container.dispose();

        MarkdownStatistics.write(0, "Startup Time(s)", String.format("%.2f", startupDuration));

        MarkdownWriter.flush();
        FileProcessor.merge();

        SimpleHttpServer.stop();

        String endpoint = ProfilerSettings.getProperty("spring-startup-analyzer.jaeger.ui.endpoint");
        String prompt = String.format("======= spring-startup-analyzer stop, click %s to view detailed info about the startup process ======", endpoint);

        startupLogger.info(prompt);
        System.out.println(prompt);

    }

    public static <T> T getComponent(Class<T> componentType) {
        return container.getComponent(componentType);
    }

    public static <T> List<T> getComponents(Class<T> componentType) {
        return container.getComponents(componentType);
    }
}
