package com.koenv.universalminecraftapi.streams;

import java.util.Map;

public class StreamSubscription {
    private StreamSubscriber subscriber;
    private String tag;
    private String stream;
    private Map<String, String> parameters;

    public StreamSubscription(StreamSubscriber subscriber, String tag, String stream, Map<String, String> parameters) {
        this.subscriber = subscriber;
        this.tag = tag;
        this.stream = stream;
        this.parameters = parameters;
    }

    public StreamSubscriber getSubscriber() {
        return subscriber;
    }

    public String getTag() {
        return tag;
    }

    public String getStream() {
        return stream;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }
}
