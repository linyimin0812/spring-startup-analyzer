package io.github.linyimin0812

import spock.lang.Specification

/**
 * @author linyimin
 * */
class BridgeSpec extends Specification {

    private static boolean atEnter = false
    private static boolean atExit = false
    private static boolean atException = false
    
    def "test getBridge"() {
        when:
        Bridge.setBridge(Bridge.NOP_BRIDGE)
        
        then:
        Bridge.NOP_BRIDGE == Bridge.getBridge()
    }

   
    def "test setBridge"() {

        given:
        BridgeImplTest bridgeImplTest = new BridgeImplTest()

        when:
        Bridge.setBridge(bridgeImplTest)
        
        then:
        bridgeImplTest == Bridge.getBridge()
    }
    
    def "test atEnter"() {

        when:
        Bridge.setBridge(Bridge.NOP_BRIDGE)
        Bridge.atEnter(null, null, null, null, null)

        then:
        !atEnter

        when:
        Bridge.setBridge(new BridgeImplTest())
        Bridge.atEnter(null, null, null, null, null)
        
        then:
        atEnter
        
    }

    def "test atExit"() {

        when:
        Bridge.setBridge(Bridge.NOP_BRIDGE)
        Bridge.atExit(null, null, null, null, null, null)

        then:
        !atExit

        when:
        Bridge.setBridge(new BridgeImplTest())
        Bridge.atExit(null, null, null, null, null, null)
        
        then:
        atExit
    }

    def "test atExceptionExit"() {

        when:
        Bridge.setBridge(Bridge.NOP_BRIDGE)
        Bridge.atExceptionExit(null, null, null, null, null, null)

        then:
        !atException

        when:

        Bridge.setBridge(new BridgeImplTest())
        Bridge.atExceptionExit(null, null, null, null, null, null)
        
        then:
        atException
    }

    private static class BridgeImplTest extends Bridge.AbstractBridge {

        @Override
        void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {
            atEnter = true
            System.out.println("BridgeImplTest.atEnter")
        }

        @Override
        void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {
            atExit = true
            System.out.println("BridgeImplTest.atExit")
        }

        @Override
        void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {
            atException = true
            System.out.println("BridgeImplTest.atExceptionExit")
        }
    }
}
