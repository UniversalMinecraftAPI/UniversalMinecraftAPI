package com.koenv.universalminecraftapi.streams;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamManager {
    private List<StreamSubscription> subscriptions = new CopyOnWriteArrayList<>();
    private List<String> streams = new ArrayList<>();

    public void send(String stream, Object message) {
        send(stream, subscription -> message);
    }

    public void send(String stream, Function<StreamSubscription, Object> converter) {
        subscriptions.stream().filter(subscription -> Objects.equals(subscription.getStream(), stream)).forEach(subscription -> {
            subscription.getSubscriber().send(new StreamMessage(converter.apply(subscription), subscription.getTag(), stream));
        });
    }

    public void subscribe(String stream, StreamSubscriber subscriber, String tag, Map<String, String> parameters) {
        if (!streams.contains(stream)) {
            throw new InvalidStreamException();
        }
        if (findSubscription(stream, subscriber).findAny().isPresent()) {
            throw new DuplicateSubscriptionException();
        }
        subscriptions.add(new StreamSubscription(subscriber, tag, stream, parameters));
    }

    public void subscribe(String stream, StreamSubscriber subscriber, String tag) {
        subscribe(stream, subscriber, tag, new HashMap<>());
    }

    public Stream<StreamSubscription> findSubscription(String stream, StreamSubscriber subscriber) {
        return subscriptions.stream()
                .filter(subscription -> Objects.equals(subscription.getStream(), stream))
                .filter(subscription -> subscription.getSubscriber().matches(subscriber));
    }

    public Stream<StreamSubscription> findSubscription(String stream, StreamSubscriber subscriber, String tag) {
        return findSubscription(stream, subscriber).filter(subscription -> Objects.equals(subscription.getTag(), tag));
    }

    public int unsubscribe(String stream, StreamSubscriber subscriber, String tag) {
        List<StreamSubscription> removeSubscriptions = findSubscription(stream, subscriber, tag)
                .collect(Collectors.toList());

        if (subscriptions.removeAll(removeSubscriptions)) {
            return removeSubscriptions.size();
        }

        return 0;
    }

    public int unsubscribe(StreamSubscriber subscriber) {
        List<StreamSubscription> removeSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.getSubscriber().matches(subscriber))
                .collect(Collectors.toList());

        if (subscriptions.removeAll(removeSubscriptions)) {
            return removeSubscriptions.size();
        }

        return 0;
    }

    public int getSubscriptionCount() {
        return subscriptions.size();
    }

    public void registerStream(String stream) {
        streams.add(stream);
    }

    public List<String> getStreams() {
        return streams;
    }
}
