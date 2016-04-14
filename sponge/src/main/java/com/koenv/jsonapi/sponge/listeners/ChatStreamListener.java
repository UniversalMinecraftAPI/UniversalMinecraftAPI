package com.koenv.jsonapi.sponge.listeners;

import com.koenv.jsonapi.streams.StreamManager;
import com.koenv.jsonapi.streams.models.ChatMessage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

public class ChatStreamListener {
    public static final String STREAM_NAME = "chat";

    private StreamManager streamManager;

    public ChatStreamListener(StreamManager streamManager) {
        this.streamManager = streamManager;
        streamManager.registerStream(STREAM_NAME);
    }

    @Listener(order = Order.POST)
    public void onChatEvent(@NotNull MessageChannelEvent.Chat event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause().first(Player.class).isPresent()) {
            streamManager.send(STREAM_NAME, new ChatMessage(event.getCause().first(Player.class).get().getName(), event.getMessage().toPlain()));
        } else {
            streamManager.send(STREAM_NAME, new ChatMessage(null, event.getMessage().toPlain()));
        }
    }
}
