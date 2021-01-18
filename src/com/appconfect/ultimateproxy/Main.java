package com.appconfect.ultimateproxy;

public class Main {
    public static void main(String[] args) {
        UltimateProxy.loadProxies();
        try {
            while (true)
                Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
