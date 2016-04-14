package com.koenv.jsonapi.http;

import com.koenv.jsonapi.http.model.JsonErrorResponse;
import com.koenv.jsonapi.http.model.JsonRequest;
import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.http.model.JsonSuccessResponse;
import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.util.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RequestHandler {
    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;

    public RequestHandler(ExpressionParser expressionParser, MethodInvoker methodInvoker) {
        this.expressionParser = expressionParser;
        this.methodInvoker = methodInvoker;
    }

    public List<JsonResponse> handle(String request) {
        List<JsonResponse> responses = new ArrayList<>();
        try {
            List<JsonRequest> requests = JsonRequest.fromJson(request);
            for (JsonRequest jsonRequest : requests) {
                try {
                    Expression expression = expressionParser.parse(jsonRequest.getExpression());
                    Object value = methodInvoker.invokeMethod(expression);
                    responses.add(createSuccessResponse(value, jsonRequest));
                } catch (MethodInvocationException e) {
                    responses.add(createErrorResponse(2, "Error while invoking method: " + e.getMessage(), jsonRequest));
                } catch (ParseException e) {
                    responses.add(createErrorResponse(3, "Error while parsing method: " + e.getMessage(), jsonRequest));
                }
            }
        } catch (JSONException | IllegalArgumentException e) {
            responses.add(createErrorResponse(1, "Invalid content, must be a JSON object or array", null));
        }

        return responses;
    }

    private JsonErrorResponse createErrorResponse(int code, String message, JsonRequest request) {
        return new JsonErrorResponse(code, message, request.getTag());
    }

    private JsonSuccessResponse createSuccessResponse(Object value, JsonRequest request) {
        return new JsonSuccessResponse(value, request.getTag());
    }
}
