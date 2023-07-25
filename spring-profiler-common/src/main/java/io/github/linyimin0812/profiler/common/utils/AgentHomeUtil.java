package io.github.linyimin0812.profiler.common.utils;

import java.io.File;

/**
 * @author yiminlin
 **/
public class AgentHomeUtil {

    public static String home() {
        String currentFilePath = AgentHomeUtil.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        File file = new File(currentFilePath);

        if (!file.exists()) {
            return System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator;
        }
        
        return file.getParentFile().getParent() + File.separator;
    }
}
