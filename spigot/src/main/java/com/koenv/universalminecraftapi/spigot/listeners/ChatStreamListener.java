package com.koenv.universalminecraftapi.spigot.listeners;

import com.koenv.universalminecraftapi.streams.StreamManager;
import com.koenv.universalminecraftapi.streams.models.ChatMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class ChatStreamListener implements Listener {
    public static final String STREAM_NAME = "chat";

    private StreamManager streamManager;

    public ChatStreamListener(StreamManager streamManager) {
        this.streamManager = streamManager;
        streamManager.registerStream(STREAM_NAME);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        streamManager.send(STREAM_NAME, new ChatMessage(event.getPlayer().getName(), event.getMessage()));
    }
}
