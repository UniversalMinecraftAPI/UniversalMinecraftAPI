package com.koenv.universalminecraftapi.streams.models;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;

public class ChatMessage implements JsonSerializable {
    private String player;
    private String message;

    public ChatMessage(String player, String message) {
        this.player = player;
        this.message = message;
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public JSONObject toJson(SerializerManager serializerManager) {
        JSONObject object = new JSONObject();
        object.put("player", player);
        object.put("message", message);
        return object;
    }
}
