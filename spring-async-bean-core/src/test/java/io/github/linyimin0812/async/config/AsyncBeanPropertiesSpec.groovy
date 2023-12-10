package io.github.linyimin0812.async.config

import com.google.gson.Gson
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.util.StringUtils
import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncBeanPropertiesSpec extends Specification {

    def "test isBeanPriorityLoadEnable"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def isBeanPriorityLoadEnableDefault = properties.isBeanPriorityLoadEnable()
        properties = parsePropertiesFromFile()
        def isBeanPriorityLoadEnableFromFile = properties.isBeanPriorityLoadEnable()
        then:
        !isBeanPriorityLoadEnableDefault
        isBeanPriorityLoadEnableFromFile
    }

    def "test setBeanPriorityLoadEnable"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def before = properties.isBeanPriorityLoadEnable()
        properties.setBeanPriorityLoadEnable(true)
        def after = properties.isBeanPriorityLoadEnable()

        then:
        !before
        after
    }

    def "test getBeanNames"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def beforeIsEmpty = properties.getBeanNames().isEmpty()
        properties = parsePropertiesFromFile()

        then:
        beforeIsEmpty
        properties.getBeanNames().size() == 3
        String.join(",", properties.getBeanNames()) == 'testBean,testComponentBean,testXmlBean'

    }

    def "test setBeanNames"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def beforeIsEmpty = properties.getBeanNames().isEmpty()

        properties.setBeanNames(Collections.singletonList("testBean"))

        then:
        beforeIsEmpty
        properties.getBeanNames().size() == 1
        String.join(",", properties.getBeanNames()) == 'testBean'
    }

    def "test getInitBeanThreadPoolCoreSize"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def defaultValue = properties.getInitBeanThreadPoolCoreSize()

        properties = parsePropertiesFromFile()
        def afterLoadFromFile = properties.getInitBeanThreadPoolCoreSize()

        then:
        defaultValue == Runtime.getRuntime().availableProcessors() + 1
        afterLoadFromFile == 8
    }

    def "test setInitBeanThreadPoolCoreSize"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def defaultValue = properties.getInitBeanThreadPoolCoreSize()
        properties.setInitBeanThreadPoolCoreSize(100)
        def afterSetting = properties.getInitBeanThreadPoolCoreSize()

        then:
        defaultValue == Runtime.getRuntime().availableProcessors() + 1
        afterSetting == 100
    }

    def "test getInitBeanThreadPoolMaxSize"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def defaultValue = properties.getInitBeanThreadPoolMaxSize()
        properties = parsePropertiesFromFile()
        def valueLoadFromFile = properties.getInitBeanThreadPoolMaxSize()

        then:
        defaultValue == Runtime.getRuntime().availableProcessors() + 1
        valueLoadFromFile == 16
    }

    def "test setInitBeanThreadPoolMaxSize"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        def defaultValue = properties.getInitBeanThreadPoolMaxSize()

        properties.setInitBeanThreadPoolMaxSize(100)
        def afterSetting = properties.getInitBeanThreadPoolMaxSize()

        then:
        defaultValue == Runtime.getRuntime().availableProcessors() + 1
        afterSetting == 100
    }
    
    def "test parse default"() {
        when:
        AsyncBeanProperties properties = new AsyncBeanProperties()
        then:
        !properties.isBeanPriorityLoadEnable()
        properties.getBeanNames().isEmpty()
        properties.getInitBeanThreadPoolCoreSize() == Runtime.getRuntime().availableProcessors() + 1
        properties.getInitBeanThreadPoolMaxSize() == Runtime.getRuntime().availableProcessors() + 1
    }

    def "test parse from properties"() {
        when:
        AsyncBeanProperties properties = parsePropertiesFromFile()

        then:
        properties.isBeanPriorityLoadEnable()
        properties.getBeanNames().size() == 3
        String.join(",", properties.getBeanNames()) == 'testBean,testComponentBean,testXmlBean'
        properties.getInitBeanThreadPoolCoreSize() == 8
        properties.getInitBeanThreadPoolMaxSize() == 16

    }

    static AsyncBeanProperties parsePropertiesFromFile() throws IOException {
        return AsyncBeanProperties.parse(new CustomEnvironment());
    }

    @SuppressWarnings("NullableProblems")
    private static class CustomEnvironment implements Environment {

        private final Gson GSON = new Gson();

        private final Properties properties = new Properties();

        CustomEnvironment() throws IOException {
            InputStream inputStream = AsyncBeanPropertiesSpec.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        }

        @Override
        String[] getActiveProfiles() {
            return new String[] {"dev"};
        }

        @Override
        String[] getDefaultProfiles() {
            return new String[] {"dev"};
        }

        @Override
        boolean acceptsProfiles(String... profiles) {
            return false;
        }

        @Override
        boolean acceptsProfiles(Profiles profiles) {
            return false;
        }

        @Override
        boolean containsProperty(String key) {
            return properties.containsKey(key);
        }

        @Override
        String getProperty(String key) {
            return properties.getProperty(key);
        }

        @Override
        String getProperty(String key, String defaultValue) {
            return properties.getProperty(key, defaultValue);
        }

        @Override
        <T> T getProperty(String key, Class<T> targetType) {

            return GSON.fromJson((String) properties.get(key), targetType);
        }

        @Override
        <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
            String value = properties.getProperty(key);
            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }
            return GSON.fromJson(value, targetType);
        }

        @Override
        String getRequiredProperty(String key) throws IllegalStateException {
            return (String) properties.get(key);
        }

        @Override
        <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
            return null;
        }

        @Override
        String resolvePlaceholders(String text) {
            return null;
        }

        @Override
        String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
            return null;
        }
    }
}
