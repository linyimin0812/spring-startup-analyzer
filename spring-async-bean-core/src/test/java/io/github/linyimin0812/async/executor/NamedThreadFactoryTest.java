package io.github.linyimin0812.async.executor;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author linyimin
 **/
class NamedThreadFactoryTest {

    @Test
    void newThread() {
        NamedThreadFactory factory = new NamedThreadFactory("test");
        Thread thread = factory.newThread(() -> {});

        Pattern pattern = Pattern.compile("test-(\\d+)-thread-(\\d+)");
        Matcher matcher = pattern.matcher(thread.getName());

        assertTrue(matcher.matches());
        assertFalse(thread.isDaemon());
    }
}