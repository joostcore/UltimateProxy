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

    public static synchronized ArrayList<HttpHost> loadProxies() {
        if (hosts == null) {
            ArrayList<HttpHost> tmp_hosts = new ArrayList<>();
            BasicOperations bo = new BasicOperations();
            try {
                String string = bo.basicGET("http://proxy.appconfect.com/api.php?action=load", false);
                Gson gson = new Gson();
                Proxy[] proxies = gson.fromJson(string, Proxy[].class);
                for (Proxy p : proxies) {
                    tmp_hosts.add(new HttpHost(p.getHost(), Integer.parseInt(p.getPort())));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            } catch (Different different) {
                different.printStackTrace();
            }
            UltimateProxy.hosts = tmp_hosts;

            Thread background_crawler_thread = new Thread(new WebCrawler("http://proxy.appconfect.com/proxy.html"));
            background_crawler_thread.setDaemon(true);
            background_crawler_thread.start();


            ProxyLoader prxLoader = new ProxyLoader();
            Thread background_proxy_test = new Thread(() -> {
                while (true) {
                    prxLoader.checkAllProxys(UltimateProxy.hosts);
                }
            });
            background_proxy_test.setDaemon(true);
            background_proxy_test.start();

        } else {
            return UltimateProxy.hosts;
        }
        return hosts;
    }


    public static void reloadProxies() {
        hosts = null;
        hosts = loadProxies();
    }
}
