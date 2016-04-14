package com.koenv.jsonapi.http;

import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.methods.InvokeParameters;
import com.koenv.jsonapi.methods.Invoker;

import java.util.Objects;

public class HttpInvokerParameters implements InvokeParameters {
    private Invoker invoker;
    private JsonRequest jsonRequest;

    public HttpInvokerParameters(Invoker invoker, JsonRequest jsonRequest) {
        this.invoker = invoker;
        this.jsonRequest = jsonRequest;
    }

    @Override
    public Object get(Class<?> clazz) {
        if (Objects.equals(clazz, Invoker.class)) {
            return invoker;
        }
        if (Objects.equals(clazz, JsonRequest.class)) {
            return jsonRequest;
        }
        return null;
    }
}
