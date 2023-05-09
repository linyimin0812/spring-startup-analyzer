package io.github.linyimin0812.profiler.common.markdown;

import ch.qos.logback.classic.Logger;
import io.github.linyimin0812.profiler.common.jaeger.Jaeger;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.upload.FileUploader;

import java.util.LinkedList;
import java.util.List;

/**
 * @author linyimin
 * @date 2023/05/01 15:16
 * @description
 **/
public class MarkdownWriter {

    private final static Logger logger = LogFactory.getStartupLogger();

    private final static List<MarkdownContent> contents = new LinkedList<>();

    public synchronized static void write(String content) {
        MarkdownContent markdownContent = new MarkdownContent(content);
        contents.add(markdownContent);
    }

    public synchronized static void write(int order, String content) {
        MarkdownContent markdownContent = new MarkdownContent(order, content);
        contents.add(markdownContent);
    }

    public static void upload() {

        String statistics = MarkdownStatistics.statistics();
        if (statistics.length() > 0) {
            contents.add(new MarkdownContent(0, statistics));
        }

        if (contents.size() == 0) {
            logger.info("markdown is empty.");
            return;
        }

        contents.sort(MarkdownContent::compareTo);

        StringBuilder contentBuilder = new StringBuilder();
        for (MarkdownContent content : contents) {
            contentBuilder.append(content.getContent());
        }

        FileUploader.upload(Jaeger.getServiceName() + ".md", contentBuilder.toString());

        contents.clear();

    }
}
