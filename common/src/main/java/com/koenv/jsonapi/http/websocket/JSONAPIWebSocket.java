package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.http.model.JsonErrorResponse;
import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.streams.StreamManager;
import com.koenv.jsonapi.util.json.JSONValue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebSocket
public class JSONAPIWebSocket {
    private RequestHandler requestHandler;
    private SerializerManager serializerManager;
    private StreamManager streamManager;

    public JSONAPIWebSocket() {
        this.requestHandler = JSONAPI.getInstance().getRequestHandler();
        this.serializerManager = JSONAPI.getInstance().getSerializerManager();
        this.streamManager = JSONAPI.getInstance().getStreamManager();
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        streamManager.unsubscribe(new WebSocketStreamSubscriber(session));
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println(session.getUpgradeRequest().getHeader("Authorization"));
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        List<JsonRequest> requests;
        try {
            requests = JsonRequest.fromJson(message);
        } catch (IllegalArgumentException e) {
            if (session.isOpen()) {
                JSONValue response = (JSONValue) serializerManager.serialize(Collections.singletonList(new JsonErrorResponse(1, "Invalid content, must be a JSON object or array", null)));
                session.getRemote().sendString(response.toString());
            }
            return;
        }

        List<JsonSerializable> responses = requestHandler.handle(requests, new WebSocketInvoker(null, session));

        JSONValue response = (JSONValue) serializerManager.serialize(responses);
        if (session.isOpen()) {
            session.getRemote().sendString(response.toString());
        }
    }
}
