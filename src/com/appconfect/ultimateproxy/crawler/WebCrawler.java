package com.appconfect.ultimateproxy.crawler;

import com.appconfect.ultimateproxy.exceptions.Different;
import com.appconfect.ultimateproxy.exceptions.NotFound;
import com.appconfect.ultimateproxy.network.BasicOperations;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class WebCrawler {


    BasicOperations bo = new BasicOperations();

    public WebCrawler(String startUrl) {

        try {
            start(startUrl);
        } catch (ParserException | URISyntaxException e) {

        }

    }

    private void start(String url) throws ParserException, URISyntaxException {


        ArrayList<NameValuePair> pairs = new ArrayList<>();
        BasicNameValuePair pair = new BasicNameValuePair("query", "Free+Proxy+List+txt");
        pairs.add(pair);
        String response = bo.basicPost(pairs, url);


        List<String> links = getLinksOnPage(response);
        for (String sub_url : links) {
            if (sub_url.length() > 0) {
                if (sub_url.charAt(0) == '/') {
                    sub_url = "https://" + getDomainName(url) + sub_url;
                }
                System.out.println(sub_url);
                //follow(sub_url);
            }
        }
    }

    private void follow(String url) throws ParserException, Different, IOException, NotFound, URISyntaxException {

        String response = bo.basicGET(url, false);

        searchForIPAndPortInPage(response);
        List<String> links = getLinksOnPage(response);

        //getHostOfPage
        //if Host is the same

        for (String sub_url : links) {
            if (sub_url.length() > 0) {
                if (sub_url.charAt(0) == '/') {
                    sub_url = "https://" + getDomainName(url) + sub_url;
                }
                System.out.println(sub_url);
                //follow(sub_url);
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

    private void searchForIPAndPortInPage(String htmlBody) {
    }

    private String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
