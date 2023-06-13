package io.github.linyimin0812.profiler.common.instruction;

import java.lang.instrument.Instrumentation;

/**
 * @author linyimin
 **/
public class InstrumentationHolder {

    private static Instrumentation instrumentation;

    public static void setInstrumentation(Instrumentation instrumentation) {
        InstrumentationHolder.instrumentation = instrumentation;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

}
