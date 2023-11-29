package eu.duckrealm.quackclaim.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        String url = exchange.getRequestURI().toString();
        Bukkit.broadcast(Component.text(url));

        try {
            event.handleEvent(exchange);
        } catch (IOException ignored) {}
    }
}
