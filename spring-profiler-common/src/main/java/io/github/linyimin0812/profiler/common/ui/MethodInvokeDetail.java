package io.github.linyimin0812.profiler.common.ui;

import com.google.gson.Gson;
import io.github.linyimin0812.profiler.common.utils.GsonUtil;

/**
 * @author linyimin
 **/
public class MethodInvokeDetail {
    private final String methodQualifier;
    private final long startMillis;
    private long duration;

    private Object[] args;

    public MethodInvokeDetail(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;
        this.startMillis = System.currentTimeMillis();

        if (args == null) {
            return;
        }

        Gson gson = GsonUtil.create();

        Object[] argStrList = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            try {
                argStrList[i] = gson.toJson(args[i]);
            } catch (Throwable ignored) {
                argStrList[i] = args[i].toString();
            }
        }
        this.args = argStrList;
    }

    public MethodInvokeDetail(String methodQualifier, long startMillis, long duration) {
        this.methodQualifier = methodQualifier;
        this.startMillis = startMillis;
        this.duration = duration;
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
