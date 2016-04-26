package com.koenv.universalminecraftapi.docgenerator.generator;

import com.koenv.universalminecraftapi.docgenerator.model.v1.AbstractV1Method;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueType;

import java.util.Map;

public class GeneratorUtils {
    public static void getArgumentDescriptions(Map<String, String> argumentDescriptions, Config config, AbstractV1Method method) {
        if (config.hasPath("arguments")) {
            config.getConfig("arguments").entrySet().forEach(entry -> {
                if (entry.getValue().valueType() != ConfigValueType.STRING) {
                    throw new IllegalArgumentException("Description for argument at path " + entry.getKey() + " for method " + method.getDeclaration() + " must be a string");
                }
                argumentDescriptions.put(entry.getKey(), (String) entry.getValue().unwrapped());
            });
        }
    }
}
