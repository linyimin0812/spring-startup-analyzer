package io.github.linyimin0812.async.config;

import com.google.gson.Gson;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class AsyncBeanPropertiesTest {

    @Test
    public void isBeanPriorityLoadEnable() throws IOException {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertFalse(properties.isBeanPriorityLoadEnable());
        properties = parsePropertiesFromFile();
        assertTrue(properties.isBeanPriorityLoadEnable());
    }

    @Test
    public void setBeanPriorityLoadEnable() {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertFalse(properties.isBeanPriorityLoadEnable());
        properties.setBeanPriorityLoadEnable(true);
        assertTrue(properties.isBeanPriorityLoadEnable());
    }

    @Test
    public void getBeanNames() throws IOException {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertTrue(properties.getBeanNames().isEmpty());

        properties = parsePropertiesFromFile();

        assertEquals(3, properties.getBeanNames().size());

        assertEquals("testBean,testComponentBean,testXmlBean", String.join(",", properties.getBeanNames()));
    }

    @Test
    public void setBeanNames() {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertTrue(properties.getBeanNames().isEmpty());
        properties.setBeanNames(Collections.singletonList("testBean"));
        assertEquals(1, properties.getBeanNames().size());

        assertEquals("testBean", String.join(",", properties.getBeanNames()));

    }

    @Test
    public void getInitBeanThreadPoolCoreSize() throws IOException {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolCoreSize());

        properties = parsePropertiesFromFile();

        assertEquals(8, properties.getInitBeanThreadPoolCoreSize());
    }

    @Test
    public void setInitBeanThreadPoolCoreSize() {

        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolCoreSize());

        properties.setInitBeanThreadPoolCoreSize(100);
        assertEquals(100, properties.getInitBeanThreadPoolCoreSize());
    }

    @Test
    public void getInitBeanThreadPoolMaxSize() throws IOException {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolMaxSize());

        properties = parsePropertiesFromFile();

        assertEquals(16, properties.getInitBeanThreadPoolMaxSize());
    }

    @Test
    public void setInitBeanThreadPoolMaxSize() {
        AsyncBeanProperties properties = new AsyncBeanProperties();
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolMaxSize());

        properties.setInitBeanThreadPoolMaxSize(100);
        assertEquals(100, properties.getInitBeanThreadPoolMaxSize());
    }

    @Test
    public void parse() throws IOException {
        AsyncBeanProperties properties = new AsyncBeanProperties();

        assertFalse(properties.isBeanPriorityLoadEnable());
        assertTrue(properties.getBeanNames().isEmpty());
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolCoreSize());
        assertEquals(Runtime.getRuntime().availableProcessors() + 1, properties.getInitBeanThreadPoolMaxSize());

        properties = parsePropertiesFromFile();

        assertTrue(properties.isBeanPriorityLoadEnable());
        assertEquals(3, properties.getBeanNames().size());
        assertEquals("testBean,testComponentBean,testXmlBean", String.join(",", properties.getBeanNames()));

        assertEquals(8, properties.getInitBeanThreadPoolCoreSize());
        assertEquals(16, properties.getInitBeanThreadPoolMaxSize());

    }

    public static AsyncBeanProperties parsePropertiesFromFile() throws IOException {
        return AsyncBeanProperties.parse(new CustomEnvironment());
    }

    @SuppressWarnings("NullableProblems")
    private static class CustomEnvironment implements Environment {

        private final Gson GSON = new Gson();

        private final Properties properties = new Properties();

        public CustomEnvironment() throws IOException {
            InputStream inputStream = AsyncBeanPropertiesTest.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        }

        @Override
        public String[] getActiveProfiles() {
            return new String[] {"dev"};
        }

        @Override
        public String[] getDefaultProfiles() {
            return new String[] {"dev"};
        }

        @Override
        public boolean acceptsProfiles(String... profiles) {
            return false;
        }

        @Override
        public boolean acceptsProfiles(Profiles profiles) {
            return false;
        }

        @Override
        public boolean containsProperty(String key) {
            return properties.containsKey(key);
        }

        @Override
        public String getProperty(String key) {
            return properties.getProperty(key);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return properties.getProperty(key, defaultValue);
        }

        @Override
        public <T> T getProperty(String key, Class<T> targetType) {

            return GSON.fromJson((String) properties.get(key), targetType);
        }

        @Override
        public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
            String value = properties.getProperty(key);
            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }
            return GSON.fromJson(value, targetType);
        }

        @Override
        public String getRequiredProperty(String key) throws IllegalStateException {
            return (String) properties.get(key);
        }

        @Override
        public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
            return null;
        }

        @Override
        public String resolvePlaceholders(String text) {
            return null;
        }

        @Override
        public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
            return null;
        }
    }
}