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
import org.apache.http.entity.StringEntity;
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


    public String basicGET(String url, boolean afterBreak) throws IOException, IllegalArgumentException, org.apache.http.conn.HttpHostConnectException, NotFound, Different {


        //System.out.println("Requesting :-> " + url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.RequestConfig);

        HttpResponse httpResponse = http.execute(httpGet);

        String return_string = InputToString(httpResponse.getEntity().getContent());

        httpResponse.getEntity().getContent().close();

        if (httpResponse.getStatusLine().getStatusCode() == 404) {
            //System.out.println(httpResponse.getStatusLine().getReasonPhrase());
            httpGet.completed();
            httpGet.releaseConnection();
            throw new NotFound();
        } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
            //System.out.println(httpResponse.getStatusLine().getReasonPhrase());
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
        // defining Timeouts
        // HttpHost proxy = new HttpHost("127.0.0.1", 8080);

        if (proxy != null) {
            builder.setProxy(this.proxy);
        }
        //builder.setConnectionTimeToLive(10, TimeUnit.SECONDS);
        requestBuilder.setConnectTimeout(5000);
        requestBuilder.setConnectionRequestTimeout(5000);
        requestBuilder.setSocketTimeout(25000);

        requestBuilder.setRedirectsEnabled(true);
        requestBuilder.setRelativeRedirectsAllowed(true);
        requestBuilder.setCircularRedirectsAllowed(false);


        requestBuilder.setCookieSpec(CookieSpecs.STANDARD);

        RequestConfig = requestBuilder.build();


        http = null;
        http = builder.build();

    }

    public String basicPost(String params, String url) {

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig);

            StringEntity stringEntity = new StringEntity(params);
            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = http.execute(httpPost);

            String return_string = InputToString(httpResponse.getEntity().getContent());

            httpResponse.getEntity().getContent().close();

            if (httpResponse.getStatusLine().getStatusCode() == 404) {
                httpPost.completed();
                httpPost.releaseConnection();
                throw new NotFound();
            } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
                httpPost.completed();
                httpPost.releaseConnection();

                //System.out.println(return_string);
                //System.out.println(httpResponse.getStatusLine().getReasonPhrase());
                // System.out.println(url);
                throw new Different();

            } else {
                httpPost.completed();
                httpPost.releaseConnection();
                return return_string;
            }

        } catch (IOException | IndexOutOfBoundsException ns) {
            //ns.printStackTrace();
            try {
                Thread.sleep(TIME_OUT);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return basicPost(params, url);

        } catch (Different | NotFound e) {
            e.printStackTrace();

            //System.out.println(url);
            //System.err.println("Es ist ein Fehler aufgetreten wir versuchen es erneut !");
            try {
                Thread.sleep(TIME_OUT);
            } catch (InterruptedException e1) {
            }
            return basicPost(params, url);

        }
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
