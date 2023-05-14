package io.github.linyimin0812.async;

import io.github.linyimin0812.async.config.AsyncBeanProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yiminlin
 * @date 2023/05/14 17:22
 **/
@ConfigurationProperties(prefix = AsyncBeanProperties.PREFIX)
public class AsyncSpringBeanProperties extends AsyncBeanProperties {
}
