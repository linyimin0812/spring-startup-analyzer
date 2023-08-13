package io.github.linyimin0812.profiler.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author linyimin
 **/
public class IpUtilTest {

    @Test
    public void testGetIp() {

        String ip = IpUtil.getIp();

        Assert.assertNotNull(ip);

        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);

        Assert.assertTrue(matcher.find());
    }
}