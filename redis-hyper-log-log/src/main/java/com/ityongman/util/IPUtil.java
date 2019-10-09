package com.ityongman.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Slf4j
public class IPUtil {
    /**
     * 获取终端ip地址
     * @return
     */
    public static String getIPAddr() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if(networkInterface.isLoopback()
                        || networkInterface.isVirtual()
                        || !networkInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while(inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if(null != inetAddress && inetAddress instanceof Inet4Address) {
                            log.info("get ip addr = {}", inetAddress.getHostAddress());

                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error("get ip addr fail, e = {}", e);
        }

        return null ;
    }
}
