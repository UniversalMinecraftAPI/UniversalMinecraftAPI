package com.koenv.jsonapi.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class StreamManager {
    private List<StreamSubscription> subscriptions = new CopyOnWriteArrayList<>();
    private List<String> streams = new ArrayList<>();

    public void send(String stream, Object message) {
        subscriptions.stream().filter(subscription -> Objects.equals(subscription.getStream(), stream)).forEach(subscription -> {
            subscription.getSubscriber().send(new StreamMessage(message, subscription.getTag(), stream));
        });
    }

    public void subscribe(String stream, StreamSubscriber subscriber, String tag) {
        if (!streams.contains(stream)) {
            throw new InvalidStreamException();
        }
        subscriptions.add(new StreamSubscription(subscriber, tag, stream));
    }

    public int unsubscribe(String stream, StreamSubscriber subscriber, String tag) {
        List<StreamSubscription> removeSubscriptions = subscriptions.stream()
                .filter(subscription -> Objects.equals(subscription.getTag(), tag))
                .filter(subscription -> Objects.equals(subscription.getStream(), stream))
                .filter(subscription -> subscription.getSubscriber().matches(subscriber))
                .collect(Collectors.toList());
        if (subscriptions.removeAll(removeSubscriptions)) {
            return removeSubscriptions.size();
        }
        return 0;
    }

    public void registerStream(String stream) {
        streams.add(stream);
    }

    public List<String> getStreams() {
        return streams;
    }
}
