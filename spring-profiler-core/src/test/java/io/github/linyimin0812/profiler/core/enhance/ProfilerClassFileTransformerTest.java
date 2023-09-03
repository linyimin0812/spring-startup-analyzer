package io.github.linyimin0812.profiler.core.enhance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author linyimin
 **/
class ProfilerClassFileTransformerTest {

    @Test
    void acquireJavaVersion() {
        ProfilerClassFileTransformer transformer = new ProfilerClassFileTransformer();
        String version = transformer.acquireJavaVersion();

        Assertions.assertNotNull(version);
    }
}