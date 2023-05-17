package io.github.linyimin0812.async.config;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiminlin
 * @date 2023/05/14 16:40
 **/
public class AsyncBeanProperties {

    public static final String PREFIX = "java.profiler.boost.spring.async";

    /**
     * Switch to prioritize async initialization of beans, disabled by default
     */
    private boolean asyncBeanPriorityLoadEnable = false;

    private static final String asyncBeanPriorityLoadEnableKey = String.format("%s.async.bean.priority.load.enable", PREFIX);

    private List<String> beanNames = new ArrayList<>();

    private static final String beanNamesKey = String.format("%s.bean.names", PREFIX);

    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * thread pool core size
     */
    private int asyncInitBeanThreadPoolCoreSize = CPU_COUNT + 1;

    private static final String asyncInitBeanThreadPoolCoreSizeKey = String.format("%s.async.init.bean.thread.pool.core.size", PREFIX);

    /**
     * thread pool max size
     */
    private int asyncInitBeanThreadPoolMaxSize = CPU_COUNT + 1;

    private static final String asyncInitBeanThreadPoolMaxSizeKey = String.format("%s.async.init.bean.thread.pool.max.size", PREFIX);


    public boolean isAsyncBeanPriorityLoadEnable() {
        return asyncBeanPriorityLoadEnable;
    }

    public void setAsyncBeanPriorityLoadEnable(boolean asyncBeanPriorityLoadEnable) {
        this.asyncBeanPriorityLoadEnable = asyncBeanPriorityLoadEnable;
    }

    public List<String> getBeanNames() {
        return beanNames;
    }

    public void setBeanNames(List<String> beanNames) {
        this.beanNames = beanNames;
    }

    public int getAsyncInitBeanThreadPoolCoreSize() {
        return asyncInitBeanThreadPoolCoreSize;
    }

    public void setAsyncInitBeanThreadPoolCoreSize(int asyncInitBeanThreadPoolCoreSize) {
        this.asyncInitBeanThreadPoolCoreSize = asyncInitBeanThreadPoolCoreSize;
    }

    public int getAsyncInitBeanThreadPoolMaxSize() {
        return asyncInitBeanThreadPoolMaxSize;
    }

    public void setAsyncInitBeanThreadPoolMaxSize(int asyncInitBeanThreadPoolMaxSize) {
        this.asyncInitBeanThreadPoolMaxSize = asyncInitBeanThreadPoolMaxSize;
    }

    public static AsyncBeanProperties parse(Environment environment) {

        AsyncBeanProperties properties = new AsyncBeanProperties();

        String enableStr = environment.getProperty(asyncBeanPriorityLoadEnableKey);

        if (!StringUtils.isEmpty(enableStr)) {
            properties.setAsyncBeanPriorityLoadEnable(Boolean.parseBoolean(environment.getProperty(asyncBeanPriorityLoadEnableKey)));
        }

        String beanNamesStr = environment.getProperty(beanNamesKey, "").trim();
        if (!StringUtils.isEmpty(beanNamesStr)) {
            List<String> beanNameList = new ArrayList<>();
            for (String beanName : beanNamesStr.split(",")) {
                beanName = beanName.trim();
                if (!StringUtils.isEmpty(beanName)) {
                    beanNameList.add(beanName);
                }
            }
            properties.setBeanNames(beanNameList);
        }

        String coreSizeStr = environment.getProperty(asyncInitBeanThreadPoolCoreSizeKey);

        if (!StringUtils.isEmpty(coreSizeStr)) {
            properties.setAsyncInitBeanThreadPoolCoreSize(Integer.parseInt(coreSizeStr));
        }

        String maxSizeStr = environment.getProperty(asyncInitBeanThreadPoolMaxSizeKey);
        if (!StringUtils.isEmpty(maxSizeStr)) {
            properties.setAsyncInitBeanThreadPoolMaxSize(Integer.parseInt(maxSizeStr));
        }

        return properties;

    }

}
