package io.github.linyimin0812.profiler.common.utils

import spock.lang.Specification

import java.lang.reflect.Field

/**
 * @author linyimin
 * */
class OSUtilSpec extends Specification {

    def "test platform"() {

        given:
        setStatic(OSUtil, 'platform', platform)

        when:
        def os = OSUtil.platform()

        then:
        os == result

        where:
        platform || result
        OSUtil.PlatformEnum.LINUX || 'LINUX'
        OSUtil.PlatformEnum.MACOS || 'MACOS'
        OSUtil.PlatformEnum.WINDOWS || 'WINDOWS'
    }

    def "test isLinux"() {
        when:
        setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.LINUX)
        then:
        OSUtil.isLinux()
    }

    def "test isMac"() {
        when:
        setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.MACOS)
        then:
        OSUtil.isMac()
    }

    def "test isWindows"() {
        when:
        setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.WINDOWS)
        then:
        OSUtil.isWindows()
    }

    def "test arch"() {
        when:
        setStatic(OSUtil, 'arch', arch)

        then:
        OSUtil.arch() == result

        where:
        arch || result
        'arm_32' || 'arm_32'
        'aarch_64' || 'aarch_64'
        'x86_32' || 'x86_32'
        'x86_64' || 'x86_64'

    }

    def "test isArm32"() {
        when:
        setStatic(OSUtil, 'arch', "arm_32")

        then:
        OSUtil.isArm32()
    }

    def "test isArm64"() {
        when:
        setStatic(OSUtil, 'arch', "aarch_64")

        then:
        OSUtil.isArm64()
    }

    def "test isX86"() {
        when:
        setStatic(OSUtil, 'arch', "x86_32")

        then:
        OSUtil.isX86()
    }

    def "test isX86_64"() {
        when:
        setStatic(OSUtil, 'arch', "x86_64")

        then:
        OSUtil.isX86_64()
    }

    static void setStatic(Class<?> clazz, String fieldName, Object value) {
        Field filed = clazz.getDeclaredField(fieldName)
        filed.accessible = true

        filed.set(null, value)
    }
}
