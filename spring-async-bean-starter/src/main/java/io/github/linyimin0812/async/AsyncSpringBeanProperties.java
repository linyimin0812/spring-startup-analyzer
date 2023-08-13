package io.github.linyimin0812.async;

import io.github.linyimin0812.async.config.AsyncBeanProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linyimin
 **/
@ConfigurationProperties(prefix = AsyncBeanProperties.PREFIX)
public class AsyncSpringBeanProperties extends AsyncBeanProperties {
}
