package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.methods.Invoker;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketInvoker implements Invoker {
    private Session session;

    public WebSocketInvoker(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
