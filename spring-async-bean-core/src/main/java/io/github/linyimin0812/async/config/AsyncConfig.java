package io.github.linyimin0812.async.config;

/**
 * @author yiminlin
 * @date 2023/05/14 17:28
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

}
