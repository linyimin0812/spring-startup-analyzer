package io.github.linyimin0812.spring.startup.utils

import spock.lang.Specification

/**
 * @author linyimin
 * */
class ShellUtilSpec extends Specification {

    def "test execute"() {

        when:
        def result = null
        if (OSUtil.isUnix()) {
            result = ShellUtil.execute(new String[] { "ls", "-al" });
        } else if (OSUtil.isWindows()) {
            result = ShellUtil.execute(new String[] { "dir" });
        }

        then:
        result.code == 0

    }

    def "test execute with print"() {
        when:
        def result = null
        if (OSUtil.isUnix()) {
            result = ShellUtil.execute(new String[] { "ls", "-al" }, true);
        } else if (OSUtil.isWindows()) {
            result = ShellUtil.execute(new String[] { "dir" }, true);
        }

        then:
        result.code == 0
    }

}
