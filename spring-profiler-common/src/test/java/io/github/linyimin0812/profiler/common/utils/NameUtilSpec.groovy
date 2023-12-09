package io.github.linyimin0812.profiler.common.utils

import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

/**
 * @author linyimin
 * */
@Stepwise
class NameUtilSpec extends Specification {
    def setup() {
        Class<NameUtil> appNameUtilClass = NameUtil.class

        Field appNameField = appNameUtilClass.getDeclaredField("appName")
        appNameField.setAccessible(true)
        appNameField.set(null, null)
    }

    def "test get app name from project.name" () {

        when:
        System.setProperty("project.name", "test-project")
        System.setProperty("spring.application.name", "test-application")
        System.setProperty("sun.java.command", "TestApplication.jar")

        then:
        'test-project' == NameUtil.getAppName()

    }

    def "test get app name from spring.application.name" () {
        when:
        System.clearProperty("project.name")
        System.setProperty("spring.application.name", "test-application")
        System.setProperty("sun.java.command", "TestApplication.jar")

        then:
        'test-application' == NameUtil.getAppName()
    }

    def "test get app name from command"() {
        when:
        System.clearProperty("project.name")
        System.clearProperty("spring.application.name")
        System.setProperty("sun.java.command", "TestApplication.jar")

        then:
        'TestApplication' == NameUtil.getAppName()
    }

    def "test getStartupInstanceName"() {
        when:
        System.setProperty("project.name", "application")
        def instanceName = NameUtil.getStartupInstanceName()

        then:
        instanceName != null

        String regex = '^application-(\\d{14})-(\\d+\\.\\d+\\.\\d+\\.\\d+)$'

        instanceName ==~ regex
    }

    def "test getFlameGraphHtmlName"() {
        when:
        System.setProperty("project.name", "application")
        def flameGraphHtmlName = NameUtil.getFlameGraphHtmlName()

        then:
        flameGraphHtmlName != null

        String regex = '^application-(\\d{14})-(\\d+\\.\\d+\\.\\d+\\.\\d+)-flame-graph.html$'

        flameGraphHtmlName ==~ regex
    }

    def "test getAnalysisHtmlName"() {
        when:
        System.setProperty("project.name", "application")
        def analysisHtmlName = NameUtil.getAnalysisHtmlName()

        then:
        analysisHtmlName != null

        String regex = '^application-(\\d{14})-(\\d+\\.\\d+\\.\\d+\\.\\d+)-analyzer.html$'

        analysisHtmlName ==~ regex
    }

    def "test getOutputPath"() {
        when:
        def output = NameUtil.getOutputPath()
        then:
        output.contains('/output/')
    }

    def "test getTemplatePath"() {
        when:
        def output = NameUtil.getTemplatePath()
        then:
        output.contains('/template/')
    }

}


