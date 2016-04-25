package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.ErrorCodes;
import com.koenv.universalminecraftapi.http.model.APIException;
import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.http.rest.RestException;
import com.koenv.universalminecraftapi.http.rest.RestHandler;
import com.koenv.universalminecraftapi.http.rest.RestMethod;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.json.JSONException;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import com.koenv.universalminecraftapi.util.json.JSONTokener;
import spark.Request;

import java.util.Objects;

public class RestRequestHandler {
    private RestHandler handler;

    public RestRequestHandler(RestHandler handler) {
        this.handler = handler;
    }

    public JsonSerializable handle(Request request, User user) {
        String bodyString = null;
        if (Objects.equals(request.requestMethod(), "POST")) {
            bodyString = request.body();
        }

        Object body = null;
        if (bodyString != null && !bodyString.isEmpty()) {
            if (request.contentType().startsWith("application/json")) {
                try {
                    body = new JSONTokener(bodyString).nextValue();
                } catch (JSONException e) {
                    return createErrorResponse(ErrorCodes.JSON_INVALID, "Invalid content, must be valid JSON");
                }
            }
        }

        RestMethod method = null;
        switch (request.requestMethod()) {
            case "POST":
                method = RestMethod.POST;
                break;
            case "GET":
                method = RestMethod.GET;
                break;
            default:
                return createErrorResponse(ErrorCodes.NOT_FOUND, "Invalid method " + request.requestMethod());
        }

        WebServerRestParameters parameters = new WebServerRestParameters(user, new WebServerQueryParamsMap(request.queryMap()), body, method);

        String path = request.pathInfo().substring(8);

        try {
            return createSuccessResponse(handler.handle(path, parameters));
        } catch (APIException e) {
            return createErrorResponse(e.getCode(), e.getMessage());
        } catch (RestException e) {
            return createErrorResponse(ErrorCodes.METHOD_INVOCATION_EXCEPTION, "Error while invoking method: " + e.getMessage());
        }
    }

    private ErrorResponse createErrorResponse(int code, String message) {
        return new ErrorResponse(code, message);
    }

    private SuccessResponse createSuccessResponse(Object result) {
        return new SuccessResponse(result);
    }

    public static class ErrorResponse implements JsonSerializable {
        private int code;
        private String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public JSONObject toJson(SerializerManager serializerManager) {
            JSONObject object = new JSONObject();
            object.put("code", code);
            object.put("message", message);
            return object;
        }
    }

    public static class SuccessResponse implements JsonSerializable {
        private Object value;

        public SuccessResponse(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public JSONObject toJson(SerializerManager serializerManager) {
            JSONObject object = new JSONObject();
            object.put("result", serializerManager.serialize(value));
            return object;
        }
    }
}
