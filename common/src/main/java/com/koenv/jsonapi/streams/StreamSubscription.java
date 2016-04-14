package com.koenv.jsonapi.streams;

public class StreamSubscription {
    private StreamSubscriber subscriber;
    private String tag;
    private String stream;

    public StreamSubscription(StreamSubscriber subscriber, String tag, String stream) {
        this.subscriber = subscriber;
        this.tag = tag;
        this.stream = stream;
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
}
