package io.github.linyimin0812.profiler.common.ui;

/**
 * @author linyimin
 **/
public class Statistics {
    private static final int DEFAULT_ORDER = 100;

    private final int order;
    private final String label;
    private final String value;

    public Statistics(String label, String value) {
        this(DEFAULT_ORDER, label, value);
    }

    public Statistics(int order, String label, String value) {
        this.order = order;
        this.label = label;
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
