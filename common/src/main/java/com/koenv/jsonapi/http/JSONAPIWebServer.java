package com.koenv.jsonapi.http;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.http.websocket.JSONAPIWebSocket;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONValue;

import java.util.List;

import static spark.Spark.*;

public class JSONAPIWebServer {
    private JSONAPIConfiguration configuration;
    private RequestHandler requestHandler;
    private SerializerManager serializerManager;

    public JSONAPIWebServer(JSONAPI jsonapi, JSONAPIConfiguration configuration, SerializerManager serializerManager) {
        this.configuration = configuration;
        this.requestHandler = new RequestHandler(jsonapi.getExpressionParser(), jsonapi.getMethodInvoker());
        this.serializerManager = serializerManager;
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
            if (!req.contentType().contains("application/json")) {
                halt(400, "Invalid content type");
            }
            List<JsonResponse> responses = requestHandler.handle(req.body());

            res.header("Content-Type", "application/json");
            JSONValue response = (JSONValue) serializerManager.serialize(responses);
            return response.toString(4);
        });

        init();
    }
}
