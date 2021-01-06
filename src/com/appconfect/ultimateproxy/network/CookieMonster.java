package com.appconfect.ultimateproxy.network;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

public class CookieMonster {


    public CookieStore httpCookieStore = new BasicCookieStore();

    public String getCookieString() {

        String cookie_string = "";
        for (Cookie cookie : httpCookieStore.getCookies()) {
            cookie_string = cookie_string + cookie.getName() + "=" + cookie.getValue() + ";";
        }
        return cookie_string;

    }

    public void displayCookies() {

        for (Cookie cookie : httpCookieStore.getCookies()
        ) {
            System.out.print(cookie.getName() + "=" + cookie.getValue() + ";");

        }
    }



}
