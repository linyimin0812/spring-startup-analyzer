package io.github.linyimin0812.profiler.core.enhance

import io.github.linyimin0812.profiler.core.container.EventListenerTest
import io.github.linyimin0812.profiler.core.container.IocContainer
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class EventDispatcherSpec extends Specification {

    @Shared
    EventDispatcher eventDispatcher = new EventDispatcher()

    def setupSpec() {
        IocContainer.start()
    }

    def cleanupSpec() {
        try {
            IocContainer.stop()
        } catch(Exception ignored) {

        }
    }

    def "test atEnter and atExit"() {
        when:
        eventDispatcher.atEnter(URLClassLoader, null, 'findResource', '(Ljava/lang/String;)Ljava/net/URL;', null)
        eventDispatcher.atExit(URLClassLoader, null, 'findResource', '(Ljava/lang/String;)Ljava/net/URL;', null, null)

        then:
        EventListenerTest.atEnterEvent != null
        EventListenerTest.atExitEvent != null
        EventListenerTest.atExitEvent.invokeId == EventListenerTest.atEnterEvent.invokeId

    }

    def "test atEnter and atExceptionExit"() {
        when:
        eventDispatcher.atEnter(URLClassLoader, null, 'findResource', '(Ljava/lang/String;)Ljava/net/URL;', null)
        eventDispatcher.atExceptionExit(URLClassLoader, null, 'findResource', '(Ljava/lang/String;)Ljava/net/URL;', null, null)

        then:
        EventListenerTest.atEnterEvent != null
        EventListenerTest.atExceptionExitEvent != null
        EventListenerTest.atEnterEvent.invokeId == EventListenerTest.atExceptionExitEvent.invokeId

    }
}
