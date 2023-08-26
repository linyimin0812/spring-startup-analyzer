package io.github.linyimin0812.profiler.common.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author linyimin
 **/
class IpUtilTest {

    @Test
    void testGetIp() {

        String ip = IpUtil.getIp();

        assertNotNull(ip);

        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);

        assertTrue(matcher.find());
    }
}