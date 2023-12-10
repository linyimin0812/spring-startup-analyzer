package io.github.linyimin0812.async.executor

import io.github.linyimin0812.async.config.AsyncBeanPropertiesSpec
import io.github.linyimin0812.async.config.AsyncConfig
import spock.lang.Specification

import java.util.concurrent.Future

/**
 * @author linyimin
 * */
class AsyncTaskExecutorSpec extends Specification {

    def "test submitTask"() {
        when:
        AsyncConfig.getInstance().setAsyncBeanProperties(AsyncBeanPropertiesSpec.parsePropertiesFromFile())
        AsyncTaskExecutor.submitTask(() -> {})

        then:
        AsyncTaskExecutor.getFutureList().size() == 1
    }

    def "test ensureAsyncTasksFinish"() {
        when:
        if (!AsyncTaskExecutor.isFinished()) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }

        then:
        AsyncTaskExecutor.isFinished()
    }

   def "test isFinished"() {
       when:
       if (!AsyncTaskExecutor.isFinished()) {
           AsyncTaskExecutor.ensureAsyncTasksFinish();
       }

       then:
       AsyncTaskExecutor.isFinished()
   }

    def "test getFutureList"() {
        when:
        List<Future<Object>> list = AsyncTaskExecutor.getFutureList()

        then:
        list != null
    }
}
