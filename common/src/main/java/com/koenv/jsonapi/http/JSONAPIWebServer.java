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

        webSocket("/api/v1/websocket", JSONAPIWebSocket.class); // this needs to be first otherwise the web socket doesn't work

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        get("/api/v1/request", (req, res) -> "Request");

        post("/api/v1/call", (req, res) -> {
            req.body();
            return "Request received";
        });

        init();
    }
}
