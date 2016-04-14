package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.streams.StreamSubscriber;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class WebSocketStreamSubscriber implements StreamSubscriber {
    private Session session;

    public WebSocketStreamSubscriber(Session session) {
        this.session = session;
    }

    @Override
    public void send(Object message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(JSONAPI.getInstance().getSerializerManager().serialize(message).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
