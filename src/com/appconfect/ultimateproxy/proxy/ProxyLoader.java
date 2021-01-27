package com.appconfect.ultimateproxy.proxy;

import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import com.appconfect.ultimateproxy.network.BasicOperations;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

public class ProxyLoader {


    public void uploadNewProxy(HttpHost host, long speed) throws Different, IOException, NotFound {
        BasicOperations basicOperations = new BasicOperations();
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("host", "" + host.getHostName()));
        pairs.add(new BasicNameValuePair("port", "" + host.getPort()));
        pairs.add(new BasicNameValuePair("speed", "" + speed));
        basicOperations.basicPost(pairs, "http://proxy.appconfect.com/api.php");
    }

    public void uploadProxyList(ArrayList<Proxy> host) throws Different, IOException, NotFound {
        if (host.size() > 0) {


            Proxy[] encodedProxies = new Proxy[host.size()];
            for (int i = 0; i < host.size(); i++) {
                encodedProxies[i] = host.get(i);
            }

            BasicOperations basicOperations = new BasicOperations();
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            Gson gson = new Gson();
            pairs.add(new BasicNameValuePair("proxy_list", "" + gson.toJson(encodedProxies)));
            basicOperations.basicPost(pairs, "http://proxy.appconfect.com/api.php");
        }
    }


    public void checkProxies(ArrayList<HttpHost> hosts) {

        ArrayList<Proxy> proxies = new ArrayList<>();
        for (HttpHost host : hosts) {
            int timeout = 5000;
            if (host.getHostName() != null) {
                //Intensive Check of Proxy
                long res1 = PingTester.testConnectivity(host, timeout);
                long res2 = PingTester.testConnectivity(host, timeout);
                long res3 = PingTester.testConnectivity(host, timeout);
                long res4 = PingTester.testConnectivity(host, timeout);
                long res5 = PingTester.testConnectivity(host, timeout);
                long res = (res1 + res2 + res3 + res4 + res5) / 5;

                if (res > timeout || res == -1) {

                } else {
                    System.out.println(host.getHostName() + " succeed ! with a score of " + res);
                }
                proxies.add(new Proxy(host.getHostName(), host.getPort() + "", res + ""));
            }
        }
        try {
            uploadProxyList(proxies);
        } catch (Different different) {
            different.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
    }

    public void checkProxy(HttpHost host) {
        int timeout = 7500;
        if (host.getHostName() != null) {
            long res = PingTester.testConnectivity(host, timeout);
            if (res > timeout || res == -1) {

            } else {
                System.out.println(host.getHostName() + " succeed ! with a score of " + res);
            }
            try {
                uploadNewProxy(host, res);
            } catch (Different different) {
                different.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            }
        }
    }
}
