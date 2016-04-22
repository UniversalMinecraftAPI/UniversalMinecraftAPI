package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.ErrorCodes;
import com.koenv.universalminecraftapi.http.model.*;
import com.koenv.universalminecraftapi.methods.Invoker;
import com.koenv.universalminecraftapi.methods.MethodAccessDeniedException;
import com.koenv.universalminecraftapi.methods.MethodInvocationException;
import com.koenv.universalminecraftapi.methods.MethodInvoker;
import com.koenv.universalminecraftapi.parser.ExpressionParser;
import com.koenv.universalminecraftapi.parser.ParseException;
import com.koenv.universalminecraftapi.parser.expressions.Expression;
import com.koenv.universalminecraftapi.util.json.JSONException;

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

    public JsonSerializable handle(JsonRequest request, Invoker invoker) {
        try {
            Expression expression = expressionParser.parse(request.getExpression());
            Object value = methodInvoker.invokeMethod(expression, new HttpInvokerParameters(invoker, request));
            return createSuccessResponse(value, request);
        } catch (MethodInvocationException e) {
            return createErrorResponse(ErrorCodes.METHOD_INVOCATION_EXCEPTION, "Error while invoking method: " + e.getMessage(), request);
        } catch (ParseException e) {
            return createErrorResponse(ErrorCodes.PARSE_ERROR, "Error while parsing method: " + e.getMessage(), request);
        } catch (MethodAccessDeniedException e) {
            return createErrorResponse(ErrorCodes.ACCESS_DENIED, e.getMessage(), request);
        } catch (APIException e) {
            return createErrorResponse(e.getCode(), e.getMessage(), request);
        }
    }

    public List<JsonSerializable> handle(List<JsonRequest> requests, Invoker invoker) {
        return requests.stream().map(jsonRequest -> handle(jsonRequest, invoker)).collect(Collectors.toList());
    }

    public List<JsonSerializable> handle(String request, Invoker invoker) {
        try {
            return handle(JsonRequest.fromJson(request), invoker);
        } catch (JSONException | IllegalArgumentException e) {
            return Collections.singletonList(createErrorResponse(ErrorCodes.JSON_INVALID, "Invalid content, must be a JSON object or array", null));
        }
    }

    private JsonErrorResponse createErrorResponse(int code, String message, JsonRequest request) {
        return new JsonErrorResponse(code, message, request != null ? request.getTag() : "");
    }

    private JsonSuccessResponse createSuccessResponse(Object value, JsonRequest request) {
        return new JsonSuccessResponse(value, request.getTag());
    }
}
