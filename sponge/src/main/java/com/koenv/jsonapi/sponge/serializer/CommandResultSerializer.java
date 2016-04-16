package com.koenv.jsonapi.sponge.serializer;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;
import org.spongepowered.api.command.CommandResult;

public class CommandResultSerializer implements Serializer<CommandResult> {
    @Override
    public Object toJson(CommandResult object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("successCount", object.getSuccessCount().orElse(null));
        json.put("affectedBlocks", object.getAffectedBlocks().orElse(null));
        json.put("affectedEntities", object.getAffectedEntities().orElse(null));
        json.put("affectedItems", object.getAffectedItems().orElse(null));
        json.put("queryResult", object.getQueryResult().orElse(null));
        return json;
    }
}
