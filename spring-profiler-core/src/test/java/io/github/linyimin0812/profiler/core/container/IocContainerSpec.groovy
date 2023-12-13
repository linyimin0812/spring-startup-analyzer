package io.github.linyimin0812.profiler.core.container

import io.github.linyimin0812.profiler.api.EventListener
import io.github.linyimin0812.profiler.api.Lifecycle
import io.github.linyimin0812.profiler.core.http.SimpleHttpServer
import io.github.linyimin0812.profiler.core.http.SimpleHttpServerSpec
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
/**
 * @author linyimin
 * */
@Stepwise
class IocContainerSpec extends Specification {

    def "test copyFile"() {

        given:
        URL srcURL = IocContainerSpec.class.getClassLoader().getResource("src/empty.txt")
        Path destPath = Paths.get(srcURL.toURI()).getParent().getParent()
        Path destFilePath = Paths.get(destPath.toString(), "empty.txt")
        Files.deleteIfExists(destFilePath)

        when:
        IocContainer.copyFile(srcURL.getPath(), destPath.toString() + '/empty.txt')

        then:
        Files.exists(destFilePath)

        cleanup:
        Files.deleteIfExists(destFilePath)
    }

    def "test start"() {
        when:
        IocContainer.start()

        then:
        IocContainer.getComponent(LifecycleTest.class) != null
        SimpleHttpServerSpec.isURLAvailable(SimpleHttpServer.endpoint() + "/hello")
        IocContainer.isStarted()

        cleanup:
        try {
            IocContainer.stop()
        } catch (Exception ignored) {

        }

    }

    def "test getComponent"() {
        when:
        LifecycleTest lifecycleTest = IocContainer.getComponent(LifecycleTest.class)

        then:
        lifecycleTest != null

        when:
        EventListenerTest eventListenerTest = IocContainer.getComponent(EventListenerTest.class)

        then:
        eventListenerTest != null
    }

    def "test getComponents"() {
        when:
        List<Lifecycle> lifecycleList = IocContainer.getComponents(Lifecycle.class)

        then:
        lifecycleList.size() == 2

        when:
        List<EventListener> eventListeners = IocContainer.getComponents(EventListener.class)

        then:
        eventListeners.size() == 2
    }

    def "test stop"() {
        when:
        LifecycleTest lifecycleTest = IocContainer.getComponent(LifecycleTest.class)


        then:
        lifecycleTest != null

        when:
        try {
            IocContainer.stop()
        } catch (Exception ignored) {

        }

        then:
        IocContainer.isStopped()
    }

}
