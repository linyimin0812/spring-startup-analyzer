package io.github.linyimin0812.spring.startup.utils

import io.github.linyimin0812.spring.startup.constant.Constants
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author linyimin
 * */
class ModuleUtilSpec extends Specification {


    @Shared
    final Path home = Paths.get(System.getProperty(Constants.USER_DIR));

    def "test getModulePaths"() {
        when:
        List<Path> paths = ModuleUtil.getModulePaths()
        String module = paths.get(0).getFileName().toString()

        then:
        module == 'spring-startup-cli'
        paths.size() == 1
    }

    def "test compile"() {
        when:
        def isCompile = ModuleUtil.compile(home.getParent())

        then: isCompile
    }

    def "test isMaven"() {
        when:
        def isMaven = ModuleUtil.isMaven(home)

        then: isMaven
    }

    def "test isGradle"() {
        when:
        def isGradle = ModuleUtil.isGradle(home)


        then: !isGradle
    }

    def "test hasMvnW"() {
        when:

        def isMvnW = ModuleUtil.hasGradleW(home)

        then: !isMvnW
    }

    def "test hasGradleW"() {
        when:
        def hasGradleW = ModuleUtil.hasGradleW(home)

        then: !hasGradleW
    }

    def "test buildWithMaven"() {
        when:
        def isBuildWithMaven = ModuleUtil.buildWithMaven(home.getParent())

        then: isBuildWithMaven
    }

    def "test buildWithGradle"() {
        when:
        def isBuildWithGradle = ModuleUtil.buildWithGradle(home.getParent())

        then: !isBuildWithGradle
    }
}
