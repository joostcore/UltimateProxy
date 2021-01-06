package com.appconfect.ultimateproxy;

import com.appconfect.ultimateproxy.crawler.WebCrawler;
import com.appconfect.ultimateproxy.proxy.ProxyLoader;

public class Main {

    public static void main(String[] args) {
        //ProxyLoader.loadFromTxt();
        //ProxyLoader.checkAllProxys();

        WebCrawler webCrawler = new WebCrawler("https://www.startpage.com/sp/search");
    }
}
