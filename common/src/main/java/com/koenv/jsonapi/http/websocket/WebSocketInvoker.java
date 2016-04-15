package com.koenv.jsonapi.http.websocket;

import com.koenv.jsonapi.http.model.BaseHttpInvoker;
import com.koenv.jsonapi.users.model.User;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketInvoker extends BaseHttpInvoker {
    private Session session;

    public WebSocketInvoker(User user, Session session) {
        super(user);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
