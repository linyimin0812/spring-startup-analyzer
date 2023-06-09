package io.github.linyimin0812.profiler.common.utils;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * @author linyimin
 * @date 2023/05/07 19:04
 **/
public class IpUtil {

    private static Boolean isJaegerReachable = null;

    public static String getIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException ignored ) {
            return "127.0.0.1";
        }

        return "127.0.0.1";
    }

    public static boolean isJaegerReachable() {
        if (isJaegerReachable != null) {
            return isJaegerReachable;
        }

        String endpoint = ProfilerSettings.getProperty("spring-startup-analyzer.jaeger.ui.endpoint");

        if (endpoint == null || endpoint.isEmpty()) {
            isJaegerReachable = false;
            return isJaegerReachable;
        }

        try {
            URL url = new URL(endpoint);
            Socket socket = new Socket(url.getHost(), url.getPort());
            isJaegerReachable = true;
            socket.close();
        } catch (IOException e) {
            isJaegerReachable = false;
        }

        return isJaegerReachable;
    }
}
