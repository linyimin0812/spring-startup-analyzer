package io.github.linyimin0812.profiler.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static java.lang.reflect.Modifier.TRANSIENT;

/**
 * @author banzhe
 **/
public class GsonUtil {

    public static Gson create() {
        return new GsonBuilder()
                .disableJdkUnsafe()
                .enableComplexMapKeySerialization()
                .create();
    }

}
