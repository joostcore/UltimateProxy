package com.appconfect.ultimateproxy.network;

import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class BasicOperations {


    CookieMonster cookieMonster = new CookieMonster();
    HttpClient http = null;
    org.apache.http.client.config.RequestConfig RequestConfig;
    int TIME_OUT = 15000;
    HttpHost proxy = null;

    public BasicOperations(HttpHost proxy) {
        this.proxy = proxy;
        buildHttpClient();

    }

    public BasicOperations(HttpHost proxy, int timeOut) {
        this.proxy = proxy;
        this.TIME_OUT = timeOut;
        buildHttpClient();
    }


    public BasicOperations() {

        buildHttpClient();

    }


    public String basicGET(String url) throws IOException, IllegalArgumentException, org.apache.http.conn.HttpHostConnectException, NotFound, Different {

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.RequestConfig);

        HttpResponse httpResponse = http.execute(httpGet);

        String return_string = InputToString(httpResponse.getEntity().getContent());

        httpResponse.getEntity().getContent().close();

        if (httpResponse.getStatusLine().getStatusCode() == 404) {
            httpGet.completed();
            httpGet.releaseConnection();
            throw new NotFound();
        } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
            httpGet.completed();
            httpGet.releaseConnection();
            throw new Different();
        } else {
            httpGet.completed();
            httpGet.releaseConnection();
            return return_string;
        }


    }

    public void buildHttpClient() {

        HttpClientBuilder builder = HttpClientBuilder.create();

        builder.setDefaultCookieStore(cookieMonster.httpCookieStore);
        builder.setRedirectStrategy(new LaxRedirectStrategy());
        //builder.disableCookieManagement();

        org.apache.http.client.config.RequestConfig.Builder requestBuilder = org.apache.http.client.config.RequestConfig.custom();

        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        if (proxy != null) {
            builder.setProxy(this.proxy);
        }
        requestBuilder.setConnectTimeout(this.TIME_OUT);
        requestBuilder.setConnectionRequestTimeout(this.TIME_OUT);
        requestBuilder.setSocketTimeout(this.TIME_OUT);

        requestBuilder.setRedirectsEnabled(true);
        requestBuilder.setRelativeRedirectsAllowed(true);
        requestBuilder.setCircularRedirectsAllowed(false);

        requestBuilder.setCookieSpec(CookieSpecs.STANDARD);

        RequestConfig = requestBuilder.build();

        http = null;
        http = builder.build();

    }

    public String basicPost(List<NameValuePair> params, String url) throws IOException, NotFound, Different {

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig);


        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
        urlEncodedFormEntity.setContentEncoding("utf-8");
        httpPost.setEntity(urlEncodedFormEntity);


        //System.out.println(InputToString(httpPost.getEntity().getContent()));
        HttpResponse httpResponse = http.execute(httpPost);

        String return_string = InputToString(httpResponse.getEntity().getContent());

        httpResponse.getEntity().getContent().close();

        if (httpResponse.getStatusLine().getStatusCode() == 404) {
            httpPost.completed();
            httpPost.releaseConnection();
            //System.out.println(url);
            throw new NotFound();
        } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
            httpPost.completed();
            httpPost.releaseConnection();
            //System.out.println(url);
            throw new Different();

        } else {
            httpPost.completed();
            httpPost.releaseConnection();
            return return_string;
        }


    }

    public String InputToString(InputStream in) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Reader reader = new InputStreamReader(in);
            int data = reader.read();


            while (data != -1) {
                stringBuilder.append((char) data);
                data = reader.read();
            }
            return stringBuilder.toString();
        } catch (IOException ex) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
