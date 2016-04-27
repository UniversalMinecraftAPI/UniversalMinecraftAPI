package com.koenv.universalminecraftapi.streams;

import com.koenv.universalminecraftapi.ErrorCodes;
import com.koenv.universalminecraftapi.UniversalMinecraftAPI;
import com.koenv.universalminecraftapi.http.model.APIException;
import com.koenv.universalminecraftapi.http.model.JsonRequest;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.http.websocket.WebSocketInvoker;
import com.koenv.universalminecraftapi.http.websocket.WebSocketStreamSubscriber;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import com.koenv.universalminecraftapi.methods.Invoker;
import com.koenv.universalminecraftapi.methods.OptionalParam;
import com.koenv.universalminecraftapi.permissions.RequiresPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@APINamespace("streams")
public class StreamMethods {
    @APIMethod
    @RequiresPermission("streams.subscribe")
    public static boolean subscribe(Invoker invoker, JsonRequest request, String stream, @OptionalParam Map<Object, Object> parameters) {
        if (!(invoker instanceof WebSocketInvoker)) {
            throw new APIException("Subscriptions only work while connected to a web socket", ErrorCodes.INVALID_STREAM_USAGE);
        }

        WebSocketInvoker webSocketInvoker = (WebSocketInvoker) invoker;

        if (!webSocketInvoker.getUser().hasPermission("streams." + stream)) {
            throw new APIException("No access to stream " + stream, ErrorCodes.ACCESS_DENIED);
        }

        Map<String, String> params = new HashMap<>();

        if (parameters != null) {
            parameters.forEach((key, value) -> params.put(key.toString(), value.toString()));
        }

        StreamSubscriber streamSubscriber = new WebSocketStreamSubscriber(webSocketInvoker.getSession());

        try {
            UniversalMinecraftAPI.getInstance().getStreamManager().subscribe(stream, streamSubscriber, request.getTag(), params);
        } catch (InvalidStreamException e) {
            throw new APIException("Invalid stream: " + stream, ErrorCodes.INVALID_STREAM);
        } catch (DuplicateSubscriptionException e) {
            final String[] tag = {null};
            UniversalMinecraftAPI.getInstance().getStreamManager().findSubscription(stream, streamSubscriber).findFirst().ifPresent(subscription -> tag[0] = subscription.getTag());
            throw new APIException("Duplicate stream for: " + stream + " with tag " + tag[0], ErrorCodes.DUPLICATE_SUBSCRIPTION);
        }

        return true;
    }

    @APIMethod
    @RequiresPermission("streams.unsubscribe")
    public static int unsubscribe(Invoker invoker, JsonRequest request, String stream) {
        if (!(invoker instanceof WebSocketInvoker)) {
            throw new APIException("Subscriptions only work while connected to a web socket", ErrorCodes.INVALID_STREAM_USAGE);
        }

        WebSocketInvoker webSocketInvoker = (WebSocketInvoker) invoker;

        if (!webSocketInvoker.getUser().hasPermission("streams." + stream)) {
            throw new APIException("No access to stream " + stream, ErrorCodes.ACCESS_DENIED);
        }

        StreamSubscriber streamSubscriber = new WebSocketStreamSubscriber(webSocketInvoker.getSession());

        return UniversalMinecraftAPI.getInstance().getStreamManager().unsubscribe(stream, streamSubscriber, request.getTag());
    }

    @APIMethod
    @RestResource("streams")
    public static List<String> listStreams() {
        return UniversalMinecraftAPI.getInstance().getStreamManager().getStreams();
    }

    @APIMethod
    @RestResource("streams/subscriptions/count")
    public static int subscriptionCount() {
        return UniversalMinecraftAPI.getInstance().getStreamManager().getSubscriptionCount();
    }
}
