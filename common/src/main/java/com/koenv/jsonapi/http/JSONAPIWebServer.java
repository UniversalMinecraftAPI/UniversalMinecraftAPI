package com.koenv.jsonapi.http;

import com.koenv.jsonapi.http.websocket.JSONAPIWebSocket;
import org.jetbrains.annotations.Nullable;

import static spark.Spark.*;

public class JSONAPIWebServer {

    /**
     * Starts the web server
     *
     * @param ipAddress The IP address to bind to or `null` if binding to all interfaces
     * @param port      The port to bind to or a value less than 1 if the port should be chosen automatically
     */
    public void start(@Nullable String ipAddress, int port) {
        if (ipAddress != null) {
            ipAddress(ipAddress);
        }
        if (port > 0) {
            port(port);
        }

        webSocket("/api/v1/websocket", JSONAPIWebSocket.class);

        before("/api/v1/*", (request, response) -> {
            // halt(401, "Unauthorized");
        });

        get("/api/v1/request", (req, res) -> "Request");

        init();
    }
}
