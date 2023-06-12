package io.github.linyimin0812.profiler.common.ui;

import java.util.List;

/**
 * @author linyimin
 **/
public class MethodInvokeMetrics {

    private final String method;
    private final long invokeCount;
    private final long totalCost;
    private final String averageCost;

    private final List<MethodInvokeDetail> invokeDetails;

    public MethodInvokeMetrics(String method, long invokeCount, long totalCost, double averageCost, List<MethodInvokeDetail> invokeDetails) {
        this.method = method;
        this.invokeCount = invokeCount;
        this.totalCost = totalCost;
        this.averageCost = String.format("%.2f", averageCost);
        this.invokeDetails = invokeDetails;
    }

    public String getMethod() {
        return method;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public String getAverageCost() {
        return averageCost;
    }

    public List<MethodInvokeDetail> getInvokeDetails() {
        return invokeDetails;
    }

    public long getInvokeCount() {
        return invokeCount;
    }
}
