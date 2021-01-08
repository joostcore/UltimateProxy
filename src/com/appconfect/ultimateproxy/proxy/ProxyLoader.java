package com.appconfect.ultimateproxy.proxy;

import com.appconfect.ultimateproxy.crawler.WebCrawler;
import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyLoader {


    public static void checkAllProxys(ArrayList<HttpHost> hosts) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int timeout = 15000;

                for (HttpHost host : hosts) {
                    if (host.getHostName() != null) {
                        System.out.println("Testing : " + host.getHostName());
                        long res = PingTester.testConnectivity(host, timeout);
                        if (res > timeout || res == -1) {

                        } else {
                            System.out.println(host.getHostName() + " succeed ! with a score of " + res);

                        }
                        try {
                            WebCrawler.uploadNewProxy(host);
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
        }).start();
    }

    public static long checkProxy(HttpHost host) {

        int timeout = 15000;

        if (host.getHostName() != null) {
            System.out.println("Testing : " + host.getHostName());
            long res = PingTester.testConnectivity(host, timeout);
            if (res > timeout || res == -1) {

            } else {
                System.out.println(host.getHostName() + " succeed ! with a score of " + res);

                return res;
            }

        }
        return -1;
    }

    public static ArrayList<HttpHost> scrapeFromString(String string) {

        ArrayList<HttpHost> hosts = new ArrayList<>();

        Pattern ip_pattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(:|<\\/td><td>)[0-9]{1,5}");
        //Pattern ip_pattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
        //Pattern port_pattern = Pattern.compile("[0-9]{2,5}");
        Matcher ip_matcher = ip_pattern.matcher(string);
        //Matcher port_matcher = port_pattern.matcher(string);
        while (ip_matcher.find()) {
            String adress = ip_matcher.group();
            adress = adress.replace("</td><td>", ":");
            String[] inet = adress.split(":");
            adress = inet[0];
            int port = Integer.parseInt(inet[1]);
            HttpHost host = new HttpHost(adress, port, "http");

            hosts.add(host);
        }
        return hosts;
    }

}
