package io.github.linyimin0812.profiler.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
public class AgentHomeUtilTest {

    @Test
    public void home() {
        assertTrue(AgentHomeUtil.home().contains("spring-startup-analyzer"));
        System.out.println(AgentHomeUtil.home());
    }
}