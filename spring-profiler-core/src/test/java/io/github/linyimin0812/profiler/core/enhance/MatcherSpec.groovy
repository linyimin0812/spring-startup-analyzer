package io.github.linyimin0812.profiler.core.enhance

import com.alibaba.deps.org.objectweb.asm.tree.ClassNode
import spock.lang.Specification

/**
 *
 * @author linyimin
 *
 * */
class MatcherSpec extends Specification {
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

    }

    def "TestIsMatchClass"() {
    }

    def "IsMatchMethod"() {
    }
}
