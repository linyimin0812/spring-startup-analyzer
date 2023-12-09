package io.github.linyimin0812.profiler.common.utils

import com.alibaba.testable.core.tool.PrivateAccessor
import spock.lang.Specification

/**
 * @author linyimin
 * */
class OSUtilSpec extends Specification {

    def "test platform"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'platform', platform)
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
        PrivateAccessor.setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.LINUX)
        then:
        OSUtil.isLinux()
    }

    def "test isMac"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.MACOS)
        then:
        OSUtil.isMac()
    }

    def "test isWindows"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'platform', OSUtil.PlatformEnum.WINDOWS)
        then:
        OSUtil.isWindows()
    }

    def "test arch"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'arch', arch)
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
        PrivateAccessor.setStatic(OSUtil, 'arch', "arm_32")
        then:
        OSUtil.isArm32()
    }

    def "test isArm64"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'arch', "aarch_64")
        then:
        OSUtil.isArm64()
    }

    def "test isX86"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'arch', "x86_32")
        then:
        OSUtil.isX86()
    }

    def "test isX86_64"() {
        when:
        PrivateAccessor.setStatic(OSUtil, 'arch', "x86_64")
        then:
        OSUtil.isX86_64()
    }

}
