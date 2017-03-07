package com.koenv.universalminecraftapi.streams.models;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

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
    public void toJson(JSONWriter writer, SerializerManager serializerManager) {
        writer.object()
                .key("message").value(message)
                .key("level").value(level)
                .key("time").value(time)
                .endObject();
    }
}
