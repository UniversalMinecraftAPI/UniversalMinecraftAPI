package com.koenv.universalminecraftapi.http.websocket;

import com.koenv.universalminecraftapi.ErrorCodes;
import com.koenv.universalminecraftapi.UniversalMinecraftAPI;
import com.koenv.universalminecraftapi.http.RequestHandler;
import com.koenv.universalminecraftapi.http.model.JsonErrorResponse;
import com.koenv.universalminecraftapi.http.model.JsonRequest;
import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.streams.StreamManager;
import com.koenv.universalminecraftapi.users.UserManager;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.json.JSONValue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class UniversalMinecraftAPIWebSocket {
    private final Logger logger = LoggerFactory.getLogger(UniversalMinecraftAPIWebSocket.class);

    private RequestHandler requestHandler;
    private SerializerManager serializerManager;
    private StreamManager streamManager;
    private UserManager userManager;

    private Map<Session, WebSocketInvoker> invokers = new ConcurrentHashMap<>();

    public UniversalMinecraftAPIWebSocket() {
        this.requestHandler = UniversalMinecraftAPI.getInstance().getRequestHandler();
        this.serializerManager = UniversalMinecraftAPI.getInstance().getSerializerManager();
        this.streamManager = UniversalMinecraftAPI.getInstance().getStreamManager();
        this.userManager = UniversalMinecraftAPI.getInstance().getUserManager();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        User user = null;
        String authorizationHeader = session.getUpgradeRequest().getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            if (!session.getUpgradeRequest().getQueryString().isEmpty()) {
                Map<String, String> queryMap = splitQuery(session.getUpgradeRequest().getQueryString());
                if (queryMap.containsKey("key")) {
                    user = userManager.getApiKeyManager().getByAPIKey(queryMap.get("key"));
                }
            }
            if (user == null && userManager.getUser("default").isPresent()) {
                user = userManager.getUser("default").get();
            } else if (user == null) {
                logger.debug("No authentication found and no default user found for session {}",session.toString());
                if (session.isOpen()) {
                    session.getRemote().sendString(
                            getErrorResponse(ErrorCodes.INVALID_CREDENTIALS, "No authentication found and no default user found"),
                            new CloseCallback(session, 401, "No authentication found and no default user found")
                    );
                }
                return;
            }
        } else {
            if (!authorizationHeader.startsWith("Basic")) {
                logger.debug("Invalid authorization header for session {}", session);
                if (session.isOpen()) {
                    session.getRemote().sendString(
                            getErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER, "Invalid Authorization header"),
                            new CloseCallback(session, 401, "Invalid Authorization header")
                    );
                }
                return;
            }

            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                logger.debug("Invalid authorization header for session {}", session);
                if (session.isOpen()) {
                    session.getRemote().sendString(
                            getErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER, "Invalid Authorization header"),
                            new CloseCallback(session, 401, "Invalid Authorization header")
                    );
                }
                return;
            }

            String username = values[0];
            String password = values[1];

            if (!userManager.checkCredentials(username, password)) {
                logger.debug("Invalid credentials for session {}", session);
                if (session.isOpen()) {
                    session.getRemote().sendString(
                            getErrorResponse(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials"),
                            new CloseCallback(session, 401, "Invalid credentials")
                    );
                }
                return;
            }

            user = userManager.getUser(username).get();
        }

        invokers.put(session, new WebSocketInvoker(user, session));
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        logger.debug("Websocket closed for session {} because {} {}", session, statusCode, reason);
        streamManager.unsubscribe(new WebSocketStreamSubscriber(session));
        invokers.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        List<JsonRequest> requests;
        try {
            requests = JsonRequest.fromJson(message);
        } catch (IllegalArgumentException e) {
            logger.debug("Invalid content, must be a JSON object or array for session {}", session);
            if (session.isOpen()) {
                session.getRemote().sendStringByFuture(getErrorResponse(ErrorCodes.JSON_INVALID, "Invalid content, must be a JSON object or array"));
            }
            return;
        }

        List<JsonSerializable> responses = requestHandler.handle(requests, invokers.get(session));

        String response = serializerManager.serialize(responses);
        if (session.isOpen()) {
            session.getRemote().sendStringByFuture(response);
        }
    }

    @OnWebSocketError
    public void error(Session session, Throwable error) {
        logger.error("Error in websocket for session {}", error);
    }

    private String getErrorResponse(int code, String message) {
        List<JsonErrorResponse> result = Collections.singletonList(new JsonErrorResponse(code, message, null));
        return serializerManager.serialize(result);
    }

    // http://stackoverflow.com/a/13592567/1608780
    private static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }

    private static class CloseCallback implements WriteCallback {
        private Session session;
        private int code;
        private String message;

        public CloseCallback(Session session, int code, String message) {
            this.session = session;
            this.code = code;
            this.message = message;
        }

        @Override
        public void writeFailed(Throwable x) {
            session.close(code, message);
        }

        @Override
        public void writeSuccess() {
            session.close(code, message);
        }
    }
}
