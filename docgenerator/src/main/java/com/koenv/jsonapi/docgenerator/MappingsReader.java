package com.koenv.jsonapi.docgenerator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MappingsReader {
    public static Map<String, String> readMappings(File file) {
        Config config = ConfigFactory.parseFile(file);

        Map<String, String> mappings = new HashMap<>();

        config.entrySet().forEach(entry -> {
            if (entry.getValue().valueType() != ConfigValueType.STRING) {
                throw new IllegalArgumentException("Invalid mapping: expected STRING, got " + entry.getValue().valueType().name() + " at " + entry.getKey());
            }
            mappings.put(entry.getKey(), (String) entry.getValue().unwrapped());
        });

        return mappings;
    }
}
