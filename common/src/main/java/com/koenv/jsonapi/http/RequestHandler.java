package com.koenv.jsonapi.http;

import com.koenv.jsonapi.methods.MethodInvocationException;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import com.koenv.jsonapi.parser.ParseException;
import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.util.json.JSONException;
import com.koenv.jsonapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestHandler {
    private ExpressionParser expressionParser;
    private MethodInvoker methodInvoker;

    public RequestHandler(ExpressionParser expressionParser, MethodInvoker methodInvoker) {
        this.expressionParser = expressionParser;
        this.methodInvoker = methodInvoker;
    }

    public Object handle(String request) {
        List<JsonRequest> requests;
        List<JSONObject> responses = new ArrayList<>();
        try {
            requests = JsonRequest.fromJson(request);
            for (JsonRequest jsonRequest : requests) {
                Expression expression = null;
                try {
                    expression = expressionParser.parse(jsonRequest.getName());
                } catch (ParseException e) {

                }
                methodInvoker.invokeMethod(expression);
            }
        } catch (JSONException e) {
            responses.add(createErrorResponse("Failed to parse JSON"));
        }

        return responses; // TODO: Change this to something useful
    }

    private JSONObject createErrorResponse(JsonRequest request) {
        JSONObject object = new JSONObject();
        object.put("is_success", false);
        object.put("error", request.getErrorMessage());
        if (request.getTag() != null) {
            object.put("tag", request.getTag());
        }
        if (request.getName() != null) {
            object.put("source", request.getName());
        }
        return object;
    }

    private JSONObject createErrorResponse(String error) {
        JSONObject object = new JSONObject();
        object.put("is_success", false);
        object.put("error", error);
        return object;
    }
}
