package io.github.linyimin0812.spring.startup.utils

import spock.lang.Specification

/**
 * @author linyimin
 * */
class GitUtilSpec extends Specification {

    def "test isGit"() {
        when:
        def isGit = GitUtil.isGit()

        then:
        isGit
    }

    def "test currentBranch"() {
        when:
        def branch = GitUtil.currentBranch()

        then:
        branch != null
    }
}
