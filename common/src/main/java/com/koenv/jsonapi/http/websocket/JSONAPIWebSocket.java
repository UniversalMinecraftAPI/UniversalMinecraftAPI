package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.http.RequestHandler;
import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONValue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.List;

@WebSocket
public class JSONAPIWebSocket {
    private RequestHandler requestHandler;
    private SerializerManager serializerManager;

    public JSONAPIWebSocket() {
        this.requestHandler = JSONAPI.getInstance().getRequestHandler();
        this.serializerManager = JSONAPI.getInstance().getSerializerManager();
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        List<JsonRequest> requests = JsonRequest.fromJson(message);

        List<JsonResponse> responses = requestHandler.handle(requests, new WebSocketInvoker(session));

        JSONValue response = (JSONValue) serializerManager.serialize(responses);
        if (session.isOpen()) {
            session.getRemote().sendString(response.toString());
        }
    }
}
