package com.github.linyimin.profiler.common.markdown;

import org.jetbrains.annotations.NotNull;

/**
 * @author linyimin
 * @date 2023/05/04 13:23
 **/
public class MarkdownContent implements Comparable<MarkdownContent> {

    private final static int DEFAULT_ORDER = 100;

    private final int order;
    private final String content;

    public MarkdownContent(String content) {
        this(DEFAULT_ORDER, content);
    }

    public MarkdownContent(int order, String content) {
        this.order = order;

        if (!content.endsWith("\n")) {
            content += "\n";
        }
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int compareTo(@NotNull MarkdownContent o) {
        return this.order - o.order;
    }
}
