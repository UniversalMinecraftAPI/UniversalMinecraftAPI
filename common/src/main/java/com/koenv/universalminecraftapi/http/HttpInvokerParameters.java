package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.http.model.JsonRequest;
import com.koenv.universalminecraftapi.methods.AbstractMethod;
import com.koenv.universalminecraftapi.methods.InvokeParameters;
import com.koenv.universalminecraftapi.methods.Invoker;

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

    @Override
    public boolean checkPermission(AbstractMethod method) {
        return invoker.checkPermission(method);
    }
}
