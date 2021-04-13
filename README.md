# Ultimate Proxy Libary

Ultimate Proxy is an social libary for crawling proxies and rating their quality.

## Here is how it works :

There is an central server wich manages all proxies ever crawled.
On using the libary method you donate two threads two the network.
This threads are crawling and testing new proxies to the network.
The crawled proxies are going to be posted back to the server. In this way
this libary can provide high quality proxies very fast.
```java
public static ArrayList<HttpHost> loadProxies()
```

## Usage
The libary is very easy to use. You have to methods you can use by  :

By using loadProxies() tested proxies will be loaded from the central server and cached locally on your machine.
Also the two threads get started if not happend yet.
```java
public static ArrayList<HttpHost> loadProxies()
```

By using reloadProxies() the cache will be deleted and your local proxylist will be updated.
If the threads are not running they will be started as well.
```java
public static void reloadProxies()
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
