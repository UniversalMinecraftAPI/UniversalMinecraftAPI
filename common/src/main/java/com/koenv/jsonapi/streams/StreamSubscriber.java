package com.koenv.jsonapi.streams;

public interface StreamSubscriber {
    void send(Object message);

    default boolean matches(StreamSubscriber other) {
        return this == other;
    }
}
