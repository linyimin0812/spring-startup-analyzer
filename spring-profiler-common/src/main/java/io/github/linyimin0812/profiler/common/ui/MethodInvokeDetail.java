package io.github.linyimin0812.profiler.common.ui;

/**
 * @author linyimin
 **/
public class MethodInvokeDetail {
    private final String methodQualifier;
    private final long startMillis;
    private long duration;

    private final Object[] args;

    public MethodInvokeDetail(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;
        this.startMillis = System.currentTimeMillis();
        this.args = args;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMethodQualifier() {
        return methodQualifier;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getDuration() {
        return duration;
    }

    public Object[] getArgs() {
        return args;
    }
}
