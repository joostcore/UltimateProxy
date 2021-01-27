package com.appconfect.ultimateproxy.proxy;

import com.appconfect.ultimateproxy.network.BasicOperations;
import org.apache.http.HttpHost;

public class PingTester {

    public static long testConnectivity(HttpHost host, int timeOut) {

        BasicOperations basicOperations = new BasicOperations(host, timeOut);
        System.out.println("Testing : " + host.getHostName() + ":" + host.getPort());
        long start = System.currentTimeMillis();
        try {
            basicOperations.basicGET("https://www.wieistmeineip.de/");
        } catch (Exception e) {
            //e.printStackTrace();
            return Integer.MAX_VALUE;
        }

        long end = System.currentTimeMillis();
        return end - start;

    }
}
