package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.ErrorCodes;
import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.http.model.JsonErrorResponse;
import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.streams.StreamManager;
import com.koenv.jsonapi.users.UserManager;
import com.koenv.jsonapi.users.model.User;
import com.koenv.jsonapi.util.json.JSONValue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class JSONAPIWebSocket {
    private RequestHandler requestHandler;
    private SerializerManager serializerManager;
    private StreamManager streamManager;
    private UserManager userManager;

    private Map<Session, WebSocketInvoker> invokers = new ConcurrentHashMap<>();

    public JSONAPIWebSocket() {
        this.requestHandler = JSONAPI.getInstance().getRequestHandler();
        this.serializerManager = JSONAPI.getInstance().getSerializerManager();
        this.streamManager = JSONAPI.getInstance().getStreamManager();
        this.userManager = JSONAPI.getInstance().getUserManager();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        User user = null;
        String authorizationHeader = session.getUpgradeRequest().getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            if (userManager.getUser("default").isPresent()) {
                user = userManager.getUser("default").get();
            } else {
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
        streamManager.unsubscribe(new WebSocketStreamSubscriber(session));
        invokers.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        List<JsonRequest> requests;
        try {
            requests = JsonRequest.fromJson(message);
        } catch (IllegalArgumentException e) {
            if (session.isOpen()) {
                session.getRemote().sendString(getErrorResponse(ErrorCodes.JSON_INVALID, "Invalid content, must be a JSON object or array"));
            }
            return;
        }

        List<JsonSerializable> responses = requestHandler.handle(requests, invokers.get(session));

        JSONValue response = (JSONValue) serializerManager.serialize(responses);
        if (session.isOpen()) {
            session.getRemote().sendString(response.toString());
        }
    }

    private String getErrorResponse(int code, String message) {
        List<JsonErrorResponse> result = Collections.singletonList(new JsonErrorResponse(code, message, null));
        JSONValue response = (JSONValue) serializerManager.serialize(result);
        return response.toString();
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
