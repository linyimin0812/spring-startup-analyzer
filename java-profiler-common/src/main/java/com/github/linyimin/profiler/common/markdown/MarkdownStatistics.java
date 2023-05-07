package com.github.linyimin.profiler.common.markdown;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linyimin
 * @date 2023/05/06 19:14
 **/
public class MarkdownStatistics {

    private static final StringBuilder STATISTICS_TEMPLATE_START = new StringBuilder("<div class=\"markdown-statistics\">\n");
    private static final StringBuilder STATISTICS_TEMPLATE_END = new StringBuilder("</div>\n\n");

    private static final List<Statistics> STATISTICS = new ArrayList<>();

    public static void write(int order, String label, String value) {
        Statistics statistics = new Statistics(order, label, value);
        STATISTICS.add(statistics);
    }

    public static void write(String label, String value) {

        Statistics statistics = new Statistics(label, value);
        STATISTICS.add(statistics);
    }

    public static String statistics() {

        if (STATISTICS.isEmpty()) {
            return "";
        }

        StringBuilder contentBuilder = new StringBuilder();

        STATISTICS.sort(Statistics::compareTo);

        for (Statistics statistics : STATISTICS) {
            contentBuilder.append("<div class=\"markdown-statistic\">\n")
                    .append(String.format("<div class=\"markdown-statistic-label\">%s</div>\n", statistics.label))
                    .append(String.format("<div class=\"markdown-statistic-value\">%s</div>\n", statistics.value))
                    .append("</div>\n");
        }

        String statistics = "<details open>\n" +
                "<summary><h1 style='display: inline'>App Statistics</h1></summary>\n" +
                "<hr/>\n" +
                STATISTICS_TEMPLATE_START +
                contentBuilder +
                STATISTICS_TEMPLATE_END +
                "</details>\n" +
                "<hr/>\n";

        STATISTICS.clear();

        return statistics;
    }

    private static class Statistics implements Comparable<Statistics> {

        private static final int DEFAULT_ORDER = 100;

        private final int order;

        public final String label;
        public final String value;

        public Statistics(String label, String value) {
            this(DEFAULT_ORDER, label, value);
        }

        public Statistics(int order, String label, String value) {
            this.order = order;
            this.label = label;
            this.value = value;
        }

        @Override
        public int compareTo(@NotNull Statistics o) {
            return this.order >= o.order ? 1 : -1;
        }
    }

}
