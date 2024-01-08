package eu.duckrealm.quackclaim.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.duckrealm.quackclaim.util.QuackConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class WebHandler implements HttpHandler {

    private final WebEvent event;
    public WebHandler(WebEvent event) {
        this.event = event;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("server", "QuackClaim-Internal");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            boolean isWebProxyIp = exchange.getRemoteAddress().getHostName().equals(QuackConfig.WEBPROXYIP);
            if(!isWebProxyIp && QuackConfig.WEBPROXYENABLED) {
                String response = "<!DOCTYPE html><html lang=en><meta charset=UTF-8><meta content=\"width=device-width,initial-scale=1\"name=viewport><title>401 Unauthorized</title><h1>401 Unauthorized</h1><p>Sorry, you are not authorized to access this page.";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(401, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            event.handleEvent(exchange);
        } catch (IOException ignored) {}
    }
}
