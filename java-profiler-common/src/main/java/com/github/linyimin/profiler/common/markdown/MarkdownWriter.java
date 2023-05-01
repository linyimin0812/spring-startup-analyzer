package com.github.linyimin.profiler.common.markdown;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.common.jaeger.Jaeger;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.upload.FileUploader;

/**
 * @author linyimin
 * @date 2023/05/01 15:16
 * @description
 **/
public class MarkdownWriter {

    private final static Logger logger = LogFactory.getStartupLogger();

    private final static StringBuilder contentBuilder = new StringBuilder();

    public synchronized static void write(String content) {
        contentBuilder.append(content);
        if (!content.endsWith("\n")) {
            contentBuilder.append("\n");
        }
    }

    public static void upload() {

        if (contentBuilder.length() == 0) {
            logger.info("markdown is empty.");
            return;
        }

        FileUploader.upload(Jaeger.getServiceName() + ".md", contentBuilder.toString());

        contentBuilder.delete(0, contentBuilder.length());

    }
}
