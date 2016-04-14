package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.methods.Invoker;
import spark.Request;
import spark.Response;

public class WebServerInvoker implements Invoker {
    private Request request;
    private Response response;

    public WebServerInvoker(Request request, Response response) {
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
