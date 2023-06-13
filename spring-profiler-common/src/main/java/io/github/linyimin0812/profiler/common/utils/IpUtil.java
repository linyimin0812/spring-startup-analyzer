package io.github.linyimin0812.profiler.common.utils;

import java.net.*;
import java.util.Enumeration;

/**
 * @author linyimin
 **/
public class IpUtil {

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
}
