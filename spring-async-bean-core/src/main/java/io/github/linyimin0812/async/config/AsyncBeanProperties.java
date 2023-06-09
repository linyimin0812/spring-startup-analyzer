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

    public static final String PREFIX = "spring-startup-analyzer.boost.spring.async";

    /**
     * Switch to prioritize async initialization of beans, disabled by default
     */
    private boolean beanPriorityLoadEnable = false;

    private static final String asyncBeanPriorityLoadEnableKey = String.format("%s.bean-priority-load-enable", PREFIX);

    private List<String> beanNames = new ArrayList<>();

    private static final String beanNamesKey = String.format("%s.bean-names", PREFIX);

    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * thread pool core size
     */
    private int initBeanThreadPoolCoreSize = CPU_COUNT + 1;

    private static final String initBeanThreadPoolCoreSizeKey = String.format("%s.init-bean-thread-pool-core-size", PREFIX);

    /**
     * thread pool max size
     */
    private int initBeanThreadPoolMaxSize = CPU_COUNT + 1;

    private static final String initBeanThreadPoolMaxSizeKey = String.format("%s.init-bean-thread-pool-max-size", PREFIX);


    public boolean isBeanPriorityLoadEnable() {
        return beanPriorityLoadEnable;
    }

    public void setBeanPriorityLoadEnable(boolean beanPriorityLoadEnable) {
        this.beanPriorityLoadEnable = beanPriorityLoadEnable;
    }

    public List<String> getBeanNames() {
        return beanNames;
    }

    public void setBeanNames(List<String> beanNames) {
        this.beanNames = beanNames;
    }

    public int getInitBeanThreadPoolCoreSize() {
        return initBeanThreadPoolCoreSize;
    }

    public void setInitBeanThreadPoolCoreSize(int initBeanThreadPoolCoreSize) {
        this.initBeanThreadPoolCoreSize = initBeanThreadPoolCoreSize;
    }

    public int getInitBeanThreadPoolMaxSize() {
        return initBeanThreadPoolMaxSize;
    }

    public void setInitBeanThreadPoolMaxSize(int initBeanThreadPoolMaxSize) {
        this.initBeanThreadPoolMaxSize = initBeanThreadPoolMaxSize;
    }

    public static AsyncBeanProperties parse(Environment environment) {

        AsyncBeanProperties properties = new AsyncBeanProperties();

        String enableStr = environment.getProperty(asyncBeanPriorityLoadEnableKey);

        if (!StringUtils.isEmpty(enableStr)) {
            properties.setBeanPriorityLoadEnable(Boolean.parseBoolean(environment.getProperty(asyncBeanPriorityLoadEnableKey)));
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

        String coreSizeStr = environment.getProperty(initBeanThreadPoolCoreSizeKey);

        if (!StringUtils.isEmpty(coreSizeStr)) {
            properties.setInitBeanThreadPoolCoreSize(Integer.parseInt(coreSizeStr));
        }

        String maxSizeStr = environment.getProperty(initBeanThreadPoolMaxSizeKey);
        if (!StringUtils.isEmpty(maxSizeStr)) {
            properties.setInitBeanThreadPoolMaxSize(Integer.parseInt(maxSizeStr));
        }

        return properties;

    }

}
