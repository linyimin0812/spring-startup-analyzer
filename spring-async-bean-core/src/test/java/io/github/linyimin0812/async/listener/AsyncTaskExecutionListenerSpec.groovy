//package io.github.linyimin0812.async.listener
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.ApplicationContext
//import org.springframework.core.Ordered
//import org.springframework.test.context.ContextConfiguration
//import spock.lang.Specification
//
//
///**
// * @author linyimin
// * */
//@ContextConfiguration("classpath:bean-context.xml")
//class AsyncTaskExecutionListenerSpec extends Specification {
//
//    @Autowired
//    private ApplicationContext applicationContext
//
//    def "test setApplicationContext"() {
//        when:
//        AsyncTaskExecutionListener listener = applicationContext.getBean(AsyncTaskExecutionListener.class)
//
//        then:
//        listener.getApplicationContext() != null
//    }
//
//    def "test getOrder"() {
//        when:
//        AsyncTaskExecutionListener listener = new AsyncTaskExecutionListener()
//
//        then:
//        listener.getOrder() == Ordered.HIGHEST_PRECEDENCE
//    }
//}
