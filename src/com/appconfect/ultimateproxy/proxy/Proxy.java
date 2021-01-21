package com.appconfect.ultimateproxy.proxy;

public class Proxy {
    String ID;
    String Host;
    String Port;
    String Speed;

    public Proxy(String host, String port, String speed) {
        Host = host;
        Port = port;
        Speed = speed;
    }

    public String getID() {
        return ID;
    }

    public String getHost() {
        return Host;
    }

    public String getPort() {
        return Port;
    }

    public String getSpeed() {
        return Speed;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "ID='" + ID + '\'' +
                ", Host='" + Host + '\'' +
                ", Port='" + Port + '\'' +
                ", Speed='" + Speed + '\'' +
                '}';
    }
}
