package io.github.linyimin0812.async.executor;

import io.github.linyimin0812.async.config.AsyncBeanPropertiesTest;
import io.github.linyimin0812.async.config.AsyncConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
public class AsyncTaskExecutorTest {

    @Test
    public void submitTask() throws IOException {
        AsyncConfig.getInstance().setAsyncBeanProperties(AsyncBeanPropertiesTest.parsePropertiesFromFile());
        AsyncTaskExecutor.submitTask(() -> {});
        assertEquals(1, AsyncTaskExecutor.getFutureList().size());
    }

    @Test
    public void ensureAsyncTasksFinish() {
        if (!AsyncTaskExecutor.isFinished()) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }

        assertTrue(AsyncTaskExecutor.isFinished());
    }

    @Test
    public void isFinished() {
        if (!AsyncTaskExecutor.isFinished()) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }

        assertTrue(AsyncTaskExecutor.isFinished());
    }

    @Test
    public void getFutureList() {
        assertNotNull(AsyncTaskExecutor.getFutureList());
    }
}