package com.appconfect.ultimateproxy;

import com.appconfect.ultimateproxy.crawler.WebCrawler;
import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import com.appconfect.ultimateproxy.network.BasicOperations;
import com.appconfect.ultimateproxy.proxy.Proxy;
import com.appconfect.ultimateproxy.proxy.ProxyLoader;
import com.google.gson.Gson;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.util.ArrayList;

public class UltimateProxy {

    private static ArrayList<HttpHost> hosts = null;

    public static ArrayList<HttpHost> loadProxies() {
        ArrayList<HttpHost> hosts = new ArrayList<>();
        new Thread(new WebCrawler("http://proxy.appconfect.com/proxy.html")).start();
        if (hosts == null) {
            BasicOperations bo = new BasicOperations();
            try {
                String string = bo.basicGET("http://proxy.appconfect.com/api.php?action=load", false);
                Gson gson = new Gson();
                Proxy[] proxies = gson.fromJson(string, Proxy[].class);
                for (Proxy p : proxies) {
                    hosts.add(new HttpHost(p.getHost(), Integer.parseInt(p.getPort())));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            } catch (Different different) {
                different.printStackTrace();
            }
            UltimateProxy.hosts = hosts;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ProxyLoader.checkAllProxys(UltimateProxy.hosts);
                }
            }).start();
        } else {
            return UltimateProxy.hosts;
        }
        return hosts;
    }

    public static void reloadProxies() {
        hosts = null;
        reloadProxies();
    }
}
