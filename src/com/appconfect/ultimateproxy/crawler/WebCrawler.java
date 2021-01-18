package com.appconfect.ultimateproxy.crawler;

import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import com.appconfect.ultimateproxy.network.BasicOperations;
import com.appconfect.ultimateproxy.proxy.ProxyLoader;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIBuilder;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler implements Runnable {


    ProxyLoader proxyLoader;
    BasicOperations bo = new BasicOperations();
    ConcurrentHashMap<String, HttpHost> crawledProxies = new ConcurrentHashMap<>();
    ArrayList<String> visitedPages = new ArrayList<>();
    String start;

    public WebCrawler(String startUrl) {
        proxyLoader = new ProxyLoader();
        start = startUrl;
    }

    private void start(String url) {
        if (!isInVisitedPages(url)) {
            addToVisitedPages(url);
            try {
                String response = null;
                response = bo.basicGET(url, false);
                List<String> links = null;
                try {
                    links = getLinksOnPage(response);
                    for (String sub_url : links) {
                        if (sub_url.length() > 0) {
                            try {
                                if (sub_url.charAt(0) == '/') {
                                    sub_url = "https://" + getDomainName(url) + sub_url;
                                }
                                String finalSub_url = sub_url;

                                if (sub_url.contains("proxy") || sub_url.contains("Proxy")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                follow(encodeValue(finalSub_url));
                                            } catch (URISyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            } catch (NotParsebleException ex) {
                                //System.out.println("Could not be parsed :" + sub_url);
                            }
                        }
                    }
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            } catch (Different different) {
                different.printStackTrace();
            }
        }
    }

    public static List<String> getLinksOnPage(final String htmlBody) throws ParserException {
        final Parser htmlParser = new Parser(htmlBody);
        final List<String> result = new LinkedList<String>();

        try {
            final NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
            for (int j = 0; j < tagNodeList.size(); j++) {
                final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
                final String loopLinkStr = loopLink.getLink();
                result.add(loopLinkStr);
            }
        } catch (ParserException e) {
            e.printStackTrace(); // TODO handle error
        }

        return result;
    }

    private void follow(String url) {
        if (!isInVisitedPages(url)) {
            addToVisitedPages(url);
            String response = null;
            try {
                response = bo.basicGET(url, false);

                ArrayList<HttpHost> crawled = scrapeFromString(response);

                for (HttpHost host : crawled) {
                    proxyLoader.checkProxy(host);
                    //addToCrawledHosts(host);
                }

                List<String> links = null;
                try {
                    links = getLinksOnPage(response);

                    for (String sub_url : links) {
                        if (sub_url.length() > 0) {
                            try {
                                if (sub_url.charAt(0) == '/') {
                                    sub_url = "https://" + getDomainName(url) + sub_url;
                                }
                                if ((sub_url.contains("proxy") || sub_url.contains("Proxy")) && !sub_url.contains("facebook.com") && !sub_url.contains("wikipedia.org")) {
                                    follow(encodeValue(sub_url));
                                }
                            } catch (NotParsebleException | URISyntaxException ex) {
                                //System.out.println("Could not be parsed :" + sub_url);
                            }
                        }
                    }
                } catch (ParserException e) {
                    // e.printStackTrace();
                }


            } catch (IOException e) {
                // e.printStackTrace();
            } catch (NotFound notFound) {
                // notFound.printStackTrace();
            } catch (Different different) {
                //different.printStackTrace();
            }

        }

    }

    private ArrayList<HttpHost> scrapeFromString(String string) {

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

    private String getDomainName(String url) throws NotParsebleException {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (NullPointerException | URISyntaxException Un) {
            throw new NotParsebleException();
        }
    }

    public synchronized void addToCrawledHosts(HttpHost host) {
        crawledProxies.put(host.getHostName(), host);
    }

    public synchronized void addToVisitedPages(String host) {
        visitedPages.add(host);
    }

    public synchronized boolean isInVisitedPages(String host) {
        return visitedPages.contains(host);
    }

    @Override
    public void run() {
        while (true) {
            start(this.start);
            try {
                Thread.sleep(3600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeValue(String value) throws URISyntaxException {

        URIBuilder ub = new URIBuilder(value);
        String url = ub.toString();

        return url;
    }

    private class NotParsebleException extends Exception {

    }
}
