package eu.duckrealm.quackclaim.webserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.duckrealm.quackclaim.util.QuackConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class WebServer {

    HttpServer httpServer = HttpServer.create(new InetSocketAddress(QuackConfig.DEFAULTPORT), 0);

    public WebServer() throws IOException {
        httpServer.start();
    }

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();

        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
				String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
				String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
				params.put(key, value);
			}
        }

        return params;
    }

    public void onRequest(String path, WebEvent event) {
        if(!QuackConfig.WEBENABLED) {
            httpServer.stop(0);
            return;
        }
        httpServer.createContext(path, new WebHandler(event));
    }
}
