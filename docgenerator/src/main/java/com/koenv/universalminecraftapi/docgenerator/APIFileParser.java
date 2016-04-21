package com.koenv.universalminecraftapi.docgenerator;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.koenv.universalminecraftapi.docgenerator.model.Platform;
import com.koenv.universalminecraftapi.docgenerator.model.PlatformDefinition;
import com.koenv.universalminecraftapi.docgenerator.model.PlatformMethods;
import com.koenv.universalminecraftapi.util.json.JSONException;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.io.IOException;

public class APIFileParser {
    private PlatformDefinition definition;

    public APIFileParser(PlatformDefinition definition) {
        this.definition = definition;
    }

    public PlatformMethods parse() throws IOException, JSONException {
        JSONObject input = new JSONObject(Files.asCharSource(definition.getFile(), Charsets.UTF_8).read());

        Platform platform = new Platform(definition.getName(), input.getJSONObject("platform"));

        return new PlatformMethods(platform, input);
    }
}
