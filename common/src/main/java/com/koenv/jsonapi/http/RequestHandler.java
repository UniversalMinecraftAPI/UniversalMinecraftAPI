package com.koenv.jsonapi.http;

import com.koenv.jsonapi.http.model.JsonErrorResponse;
import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.http.model.JsonSuccessResponse;
import com.koenv.jsonapi.methods.Invoker;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.util.json.JSONException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestHandler {
    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;

    public RequestHandler(ExpressionParser expressionParser, MethodInvoker methodInvoker) {
        this.expressionParser = expressionParser;
        this.methodInvoker = methodInvoker;
    }

    public JsonResponse handle(JsonRequest request, Invoker invoker) {
        try {
            Expression expression = expressionParser.parse(request.getExpression());
            Object value = methodInvoker.invokeMethod(expression, invoker);
            return createSuccessResponse(value, request);
        } catch (MethodInvocationException e) {
            return createErrorResponse(2, "Error while invoking method: " + e.getMessage(), request);
        } catch (ParseException e) {
            return createErrorResponse(3, "Error while parsing method: " + e.getMessage(), request);
        }
    }

    public List<JsonResponse> handle(List<JsonRequest> requests, Invoker invoker) {
        return requests.stream().map(jsonRequest -> handle(jsonRequest, invoker)).collect(Collectors.toList());
    }

    public List<JsonResponse> handle(String request, Invoker invoker) {
        try {
            return handle(JsonRequest.fromJson(request), invoker);
        } catch (JSONException | IllegalArgumentException e) {
            return Collections.singletonList(createErrorResponse(1, "Invalid content, must be a JSON object or array", null));
        }
    }

    private JsonErrorResponse createErrorResponse(int code, String message, JsonRequest request) {
        return new JsonErrorResponse(code, message, request.getTag());
    }

    private JsonSuccessResponse createSuccessResponse(Object value, JsonRequest request) {
        return new JsonSuccessResponse(value, request.getTag());
    }
}
