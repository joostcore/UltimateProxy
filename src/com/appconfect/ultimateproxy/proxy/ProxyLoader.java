package com.appconfect.ultimateproxy.proxy;

import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import com.appconfect.ultimateproxy.network.BasicOperations;
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

    public void checkAllProxys(ArrayList<HttpHost> hosts) {

        for (HttpHost host : hosts) {
            checkProxy(host);
        }

    }

    public void checkProxy(HttpHost host) {
        int timeout = 15000;
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
