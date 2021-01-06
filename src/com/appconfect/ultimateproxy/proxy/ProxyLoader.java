package com.appconfect.ultimateproxy.proxy;

import org.apache.http.HttpHost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ProxyLoader {

    public static List<HttpHost> proxyList = new ArrayList<>();
    public static List<HttpHost> provedProxyList = new ArrayList<>();

    public static void loadFromTxt() {
        try {
            File file = new File("proxy.txt");    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(":");
                HttpHost h = new HttpHost(p[0], Integer.parseInt(p[1]));
                proxyList.add(h);
            }
            fr.close();    //closes the stream and release the resources
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkAllProxys() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int timeout = 2500;

                for (HttpHost host : ProxyLoader.proxyList) {
                    if (host.getHostName() != null) {
                        System.out.println("Testing : "+ host.getHostName());
                        long res = PingTester.testConnectivity(host,timeout);
                        if (res > timeout || res == -1) {

                        }else{
                            System.out.println(host.getHostName() +" succeed ! with a score of " + res);
                            provedProxyList.add(host);
                        }
                    }
                }
            }
        }).start();
    }
}
