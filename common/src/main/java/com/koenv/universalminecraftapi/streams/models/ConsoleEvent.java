package com.koenv.universalminecraftapi.streams.models;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;

public class ConsoleEvent implements JsonSerializable {
    private String message;
    private String level;
    private long time;

    public ConsoleEvent(String message, String level, long time) {
        this.message = message;
        this.level = level;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }

    public long getTime() {
        return time;
    }

    @Override
    public JSONObject toJson(SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("message", message);
        json.put("level", level);
        json.put("time", time);
        return json;
    }
}