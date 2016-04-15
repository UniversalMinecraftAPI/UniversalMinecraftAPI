package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.users.model.User;
import spark.Request;
import spark.Response;

public class WebServerInvoker extends BaseHttpInvoker {
    private Request request;
    private Response response;

    public WebServerInvoker(User user, Request request, Response response) {
        super(user);
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
