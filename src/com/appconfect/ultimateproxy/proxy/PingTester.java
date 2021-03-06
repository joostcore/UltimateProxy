package com.appconfect.ultimateproxy.proxy;

import com.appconfect.ultimateproxy.network.BasicOperations;
import org.apache.http.HttpHost;

public class PingTester {

    public static long testConnectivity(HttpHost host, int timeOut) {

        BasicOperations basicOperations = new BasicOperations(host, timeOut);
        long start = System.currentTimeMillis();
        try {
            basicOperations.basicGET("http://proxy.appconfect.com/proxy-as-list.php");
        } catch (Exception e) {
            //e.printStackTrace();
            return Integer.MAX_VALUE;
        }
        long end = System.currentTimeMillis();
        return end - start;

    }
}
