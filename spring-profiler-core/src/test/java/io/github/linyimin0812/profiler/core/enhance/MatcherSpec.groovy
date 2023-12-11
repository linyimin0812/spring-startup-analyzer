package io.github.linyimin0812.profiler.core.enhance

import com.alibaba.deps.org.objectweb.asm.tree.ClassNode
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode
import io.github.linyimin0812.profiler.core.container.IocContainer
import spock.lang.Specification

/**
 *
 * @author linyimin
 *
 * */
class MatcherSpec extends Specification {

    def setupSpec() {
        IocContainer.start()
    }

    def cleanupSpec() {
        try {
            IocContainer.stop()
        } catch(Exception ignored) {

        }
    }

    def "IsJavaProfilerFamily"() {
        when:
        ClassNode classNode = new ClassNode(name: name, interfaces: interfaces)

        then:
        Matcher.isJavaProfilerFamily(classNode) == result

        where:
        name | interfaces || result
        'com/github/linyimin/profiler' | ['io/github/linyimin0812/profiler/api/EventListener', 'io/github/linyimin0812/profiler/api/Lifecycle'] || true
        'java/lang/String' | ['java/lang/String'] || false
    }

    def "IsMatchClass"() {
        when:
        ClassNode classNode = new ClassNode(name: name)

        then:
        Matcher.isMatchClass(classNode) == result

        where:
        name || result
        'org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory' || true
        'java/lang/String' || false
    }

    def "TestIsMatchClass"() {
        when:
        def className = name

        then:
        Matcher.isMatchClass(className) == result

        where:
        name || result
        'org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory' || true
        'java.lang.String' || false
    }

    def "test isMatchMethod with access"() {

        when:
        ClassNode classNode = new ClassNode();
        MethodNode methodNode = new MethodNode()
        methodNode.access = access

        then:
        Matcher.isMatchMethod(classNode, methodNode) == result

        where:
        access || result
        0x0400 || false
        0x0100 || false
        0x0040 || false
        0x1000 || false
        0x0080 || false
    }

    def "test isMatchMethod with method name"() {

        when:
        ClassNode classNode = new ClassNode();
        MethodNode methodNode = new MethodNode(name: methodName)

        then:
        Matcher.isMatchMethod(classNode, methodNode) == result

        where:
        methodName || result
        '<init>' || false
        '<clinit>' || false
    }

    def "test isMatchMethod with method name and method types"() {

        when:
        ClassNode classNode = new ClassNode(name: className);
        MethodNode methodNode = new MethodNode(name: methodName, desc: desc)

        then:
        Matcher.isMatchMethod(classNode, methodNode) == result

        where:
        className | methodName | desc || result
        'org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory' | 'createBean' | '(Ljava/lang/String;Lorg/springframework/beans/factory/support/RootBeanDefinition;[Ljava/lang/Object;)Ljava/lang/Object;' || true
        'org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory' | 'createBean' | '(Ljava/lang/Class;)Ljava/lang/Object;' || false
    }


}
