package io.github.linyimin0812.async.config;

/**
 * @author linyimin
 **/
public class AsyncConfig {
    private static final AsyncConfig INSTANCE = new AsyncConfig();

    private AsyncBeanProperties asyncBeanProperties;

    public static AsyncConfig getInstance() {
        return INSTANCE;
    }

    public AsyncBeanProperties getAsyncBeanProperties() {
        return this.asyncBeanProperties;
    }

    public void setAsyncBeanProperties(AsyncBeanProperties asyncBeanProperties) {
        this.asyncBeanProperties = asyncBeanProperties == null ? new AsyncBeanProperties() : asyncBeanProperties;
    }

    public boolean isAsyncBean(String beanName) {
        if (asyncBeanProperties == null) {
            asyncBeanProperties = new AsyncBeanProperties();
        }
        if (asyncBeanProperties.getBeanNames() == null) {
            return false;
        }
        return asyncBeanProperties.getBeanNames().contains(beanName);
    }

    @Override
    public String toString() {
        return "AsyncConfig{" +
                "asyncBeanProperties=" + asyncBeanProperties +
                '}';
    }
}
