package io.github.linyimin0812.async.executor;

import io.github.linyimin0812.async.config.AsyncConfig;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author linyimin
 **/
public class AsyncTaskExecutor {

    private static final Logger logger = LogFactory.getAsyncBeanLogger();

    private static ThreadPoolExecutor threadPool;

    private static boolean finished = false;

    private static final List<Future<Object>> futureList = new ArrayList<>();


    public static void submitTask(Runnable runnable) {
        if (threadPool == null) {
            threadPool = createThreadPoolExecutor();
        }

        futureList.add(threadPool.submit(runnable, null));
    }

    public static void ensureAsyncTasksFinish() {

        if (futureList.isEmpty()) {
            return;
        }

        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        finished = true;

        futureList.clear();

        threadPool.shutdown();

    }

    public static boolean isFinished() {
        return finished;
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        int threadPoolCoreSize = AsyncConfig.getInstance().getAsyncBeanProperties().getInitBeanThreadPoolCoreSize();

        int threadPollMaxSize = AsyncConfig.getInstance().getAsyncBeanProperties().getInitBeanThreadPoolMaxSize();

        logger.info(AsyncTaskExecutor.class, "create async-init-bean thread pool, corePoolSize: {}, maxPoolSize: {}.", threadPoolCoreSize, threadPoolCoreSize);

        NamedThreadFactory threadFactory = new NamedThreadFactory("async-init-bean");

        return new ThreadPoolExecutor(threadPoolCoreSize, threadPollMaxSize, 30, TimeUnit.SECONDS, new SynchronousQueue<>(),  threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    }

    public static List<Future<Object>> getFutureList() {
        return futureList;
    }
}
