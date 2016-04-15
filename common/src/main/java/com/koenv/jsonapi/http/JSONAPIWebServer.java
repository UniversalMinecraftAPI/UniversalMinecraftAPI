package com.koenv.jsonapi.http;

import com.koenv.jsonapi.ErrorCodes;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.JSONAPIProvider;
import com.koenv.jsonapi.config.JSONAPIRootConfiguration;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.http.model.WebServerInvoker;
import com.koenv.jsonapi.http.websocket.JSONAPIWebSocket;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.users.UserManager;
import com.koenv.jsonapi.users.model.User;
import com.koenv.jsonapi.util.json.JSONArray;
import com.koenv.jsonapi.util.json.JSONObject;
import com.koenv.jsonapi.util.json.JSONValue;
import spark.Spark;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static spark.Spark.*;

public class JSONAPIWebServer {
    private JSONAPIRootConfiguration configuration;
    private RequestHandler requestHandler;
    private SerializerManager serializerManager;
    private UserManager userManager;

    public JSONAPIWebServer(JSONAPIInterface jsonapi) {
        this.configuration = jsonapi.getConfiguration();
        this.requestHandler = jsonapi.getRequestHandler();
        this.serializerManager = jsonapi.getSerializerManager();
        this.userManager = jsonapi.getUserManager();
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

        before("/api/v1/*", (request, response) -> {
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                if (userManager.getUser("default").isPresent()) {
                    request.attribute("com.koenv.jsonapi.user", "default");
                } else {
                    response.header("Content-Type", "application/json");
                    halt(401, getErrorResponse(ErrorCodes.INVALID_CREDENTIALS, "No authentication found and no default user found"));
                }

                return;
            }

            if (!authorizationHeader.startsWith("Basic")) {
                response.header("Content-Type", "application/json");
                halt(401, getErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER, "Invalid Authorization header"));
                return;
            }

            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                response.header("Content-Type", "application/json");
                halt(401, getErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER, "Invalid Authorization header"));
                return;
            }

            String username = values[0];
            String password = values[1];

            if (!userManager.checkCredentials(username, password)) {
                response.header("Content-Type", "application/json");
                halt(401, getErrorResponse(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials"));
                return;
            }

            request.attribute("com.koenv.jsonapi.user", username);
        });

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        get("/api/v1/version", (request, response) -> {
            System.out.println("version");
            JSONAPIProvider provider = JSONAPI.getInstance().getProvider();

            JSONObject object = new JSONObject();

            object.put("version", provider.getJSONAPIVersion());

            JSONObject platform = new JSONObject();
            platform.put("name", provider.getPlatform());
            platform.put("version", provider.getPlatformVersion());

            object.put("platform", platform);

            return object.toString(4);
        });

        post("/api/v1/call", (request, response) -> {
            response.header("Content-Type", "application/json");
            if (!request.contentType().contains("application/json")) {
                halt(400, getErrorResponse(ErrorCodes.INVALID_CONTENT_TYPE, "Invalid content type"));
            }

            Optional<User> user = userManager.getUser(request.attribute("com.koenv.jsonapi.user"));

            if (!user.isPresent()) {
                halt(401, getErrorResponse(ErrorCodes.AUTHENTICATION_ERROR, "Authentication error"));
            }

            WebServerInvoker invoker = new WebServerInvoker(user.get(), request, response);

            List<JsonSerializable> responses = requestHandler.handle(request.body(), invoker);

            JSONValue result = (JSONValue) serializerManager.serialize(responses);
            return result.toString(4);
        });

        System.out.println("test");
    }

    public void stop() {
        Spark.stop();
    }

    private String getErrorResponse(int code, String message) {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("message", message);
        array.put(object);
        return array.toString(4);
    }
}
