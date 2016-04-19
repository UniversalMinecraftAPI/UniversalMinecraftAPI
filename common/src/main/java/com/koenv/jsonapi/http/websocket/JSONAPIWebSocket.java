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
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.api.extensions.IncomingFrames;
import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.WebSocketRemoteEndpoint;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.io.IOState;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

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
            if (!session.getUpgradeRequest().getQueryString().isEmpty()) {
                Map<String, String> queryMap = splitQuery(session.getUpgradeRequest().getQueryString());
                if (queryMap.containsKey("key")) {
                    user = userManager.getApiKeyManager().getByAPIKey(queryMap.get("key"));
                }
            }
            if (user == null && userManager.getUser("default").isPresent()) {
                user = userManager.getUser("default").get();
            } else if (user == null) {
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
                session.getRemote().sendStringByFuture(getErrorResponse(ErrorCodes.JSON_INVALID, "Invalid content, must be a JSON object or array"));
            }
            return;
        }

        List<JsonSerializable> responses = requestHandler.handle(requests, invokers.get(session));

        JSONValue response = (JSONValue) serializerManager.serialize(responses);
        if (session.isOpen()) {
            session.getRemote().sendStringByFuture(response.toString());
        }
    }

    private String getErrorResponse(int code, String message) {
        List<JsonErrorResponse> result = Collections.singletonList(new JsonErrorResponse(code, message, null));
        JSONValue response = (JSONValue) serializerManager.serialize(result);
        return response.toString();
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

    private static class LogRemoteEndpoint extends WebSocketRemoteEndpoint {
        private RemoteEndpoint delegate;

        public LogRemoteEndpoint(WebSocketRemoteEndpoint delegate) {
            super(new LogicalConnection() {
                @Override
                public void close() {

                }

                @Override
                public void close(int statusCode, String reason) {

                }

                @Override
                public void disconnect() {

                }

                @Override
                public ByteBufferPool getBufferPool() {
                    return null;
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public long getIdleTimeout() {
                    return 0;
                }

                @Override
                public IOState getIOState() {
                    return null;
                }

                @Override
                public InetSocketAddress getLocalAddress() {
                    return null;
                }

                @Override
                public long getMaxIdleTimeout() {
                    return 0;
                }

                @Override
                public WebSocketPolicy getPolicy() {
                    return null;
                }

                @Override
                public InetSocketAddress getRemoteAddress() {
                    return null;
                }

                @Override
                public WebSocketSession getSession() {
                    return null;
                }

                @Override
                public boolean isOpen() {
                    return false;
                }

                @Override
                public boolean isReading() {
                    return false;
                }

                @Override
                public void setMaxIdleTimeout(long ms) {

                }

                @Override
                public void setNextIncomingFrames(IncomingFrames incoming) {

                }

                @Override
                public void setSession(WebSocketSession session) {

                }

                @Override
                public SuspendToken suspend() {
                    return null;
                }

                @Override
                public void outgoingFrame(Frame frame, WriteCallback callback, BatchMode batchMode) {

                }

                @Override
                public void resume() {

                }
            }, null);
            this.delegate = delegate;
        }

        @Override
        public void sendBytes(ByteBuffer data) throws IOException {
            System.out.println("Sending bytes on " + Thread.currentThread().getName());
            delegate.sendBytes(data);
        }

        @Override
        public Future<Void> sendBytesByFuture(ByteBuffer data) {
            System.out.println("Sending bytes by future on " + Thread.currentThread().getName());
            return delegate.sendBytesByFuture(data);
        }

        @Override
        public void sendBytes(ByteBuffer data, WriteCallback callback) {
            System.out.println("Sending bytes by callback on " + Thread.currentThread().getName());
            delegate.sendBytes(data, callback);
        }

        @Override
        public void sendPartialBytes(ByteBuffer fragment, boolean isLast) throws IOException {
            System.out.println("Sending partial bytes on " + Thread.currentThread().getName());
            delegate.sendPartialBytes(fragment, isLast);
        }

        @Override
        public void sendPartialString(String fragment, boolean isLast) throws IOException {
            System.out.println("Sending partial string on " + Thread.currentThread().getName());
            delegate.sendPartialString(fragment, isLast);
        }

        @Override
        public void sendPing(ByteBuffer applicationData) throws IOException {
            System.out.println("Sending ping on " + Thread.currentThread().getName());
            delegate.sendPing(applicationData);
        }

        @Override
        public void sendPong(ByteBuffer applicationData) throws IOException {
            System.out.println("Sending pong on " + Thread.currentThread().getName());
            delegate.sendPong(applicationData);
        }

        @Override
        public void sendString(String text) throws IOException {
            System.out.println("Sending string on " + Thread.currentThread().getName() + ": " + text);
            delegate.sendString(text);
        }

        @Override
        public Future<Void> sendStringByFuture(String text) {
            System.out.println("Sending string by future on " + Thread.currentThread().getName());
            return delegate.sendStringByFuture(text);
        }

        @Override
        public void sendString(String text, WriteCallback callback) {
            System.out.println("Sending string on " + Thread.currentThread().getName());
            delegate.sendString(text, callback);
        }

        @Override
        public BatchMode getBatchMode() {
            return delegate.getBatchMode();
        }

        @Override
        public void flush() throws IOException {
            System.out.println("Flushing on " + Thread.currentThread().getName());
            delegate.flush();
        }
    }
}
