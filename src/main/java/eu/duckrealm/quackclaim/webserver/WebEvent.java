package eu.duckrealm.quackclaim.webserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

@FunctionalInterface
public interface WebEvent {
    void handleEvent(HttpExchange exchange) throws IOException;
}
