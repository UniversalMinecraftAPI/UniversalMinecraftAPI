package com.koenv.jsonapi.streams.models;

import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

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
