package com.koenv.jsonapi.docgenerator.generator;

import com.koenv.jsonapi.docgenerator.model.AbstractMethod;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueType;

import java.util.Map;

public class GeneratorUtils {
    public static void getArgumentDescriptions(Map<String, String> argumentDescriptions, Config config, AbstractMethod method) {
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
