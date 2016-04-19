package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.streams.StreamSubscriber;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Objects;

public class WebSocketStreamSubscriber implements StreamSubscriber {
    private Session session;

    public WebSocketStreamSubscriber(Session session) {
        this.session = session;
    }

    @Override
    public void send(Object message) {
        if (session.isOpen()) {
            session.getRemote().sendStringByFuture(JSONAPI.getInstance().getSerializerManager().serialize(message).toString());
        }
    }

    public Session getSession() {
        return session;
    }

    @Override
    public boolean matches(StreamSubscriber other) {
        if (!(other instanceof WebSocketStreamSubscriber)) {
            return false;
        }
        WebSocketStreamSubscriber subscriber = (WebSocketStreamSubscriber) other;
        return Objects.equals(getSession(), subscriber.getSession());
    }
}
