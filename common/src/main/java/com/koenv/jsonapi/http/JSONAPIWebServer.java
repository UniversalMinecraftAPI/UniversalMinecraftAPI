package com.koenv.jsonapi.http;

import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.http.websocket.JSONAPIWebSocket;

import static spark.Spark.*;

public class JSONAPIWebServer {
    private JSONAPIConfiguration configuration;

    public JSONAPIWebServer(JSONAPIConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Starts the web server
     */
    public void start() {
        if (configuration.getWebServer().getIpAddress() != null) {
            ipAddress(configuration.getWebServer().getIpAddress());
        }

        if (configuration.getWebServer().getPort() > 0) {
            port(configuration.getWebServer().getPort());
        }

        if (configuration.getWebServer().getSecure().isEnabled()) {
            WebServerSecureSection secure = configuration.getWebServer().getSecure();
            secure(secure.getKeyStoreFile(), secure.getKeystorePassword(), secure.getTrustStoreFile(), secure.getTrustStorePassword());
        }

        if (configuration.getWebServer().getThreadPool().getMaxThreads() > 0) {
            WebServerThreadPoolSection threadPool = configuration.getWebServer().getThreadPool();
            threadPool(threadPool.getMaxThreads(), threadPool.getMinThreads(), threadPool.getIdleTimeoutMillis());
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
