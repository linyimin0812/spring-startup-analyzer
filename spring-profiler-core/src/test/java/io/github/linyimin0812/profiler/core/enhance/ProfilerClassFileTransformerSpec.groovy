package io.github.linyimin0812.profiler.core.enhance

import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class ProfilerClassFileTransformerSpec extends Specification {

    @Shared
    ProfilerClassFileTransformer transformer = new ProfilerClassFileTransformer()

    def "test acquireJavaVersion"() {
        when:
        String version = transformer.acquireJavaVersion();

        then:
        version != null
    }

}
