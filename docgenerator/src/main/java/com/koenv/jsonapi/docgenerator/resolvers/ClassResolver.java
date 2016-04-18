package com.koenv.jsonapi.docgenerator.resolvers;

import com.koenv.jsonapi.docgenerator.model.JSONAPIClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClassResolver {
    private Map<String, JSONAPIClass> cache = new HashMap<>();
    private Map<String, JSONAPIClass> classes = new HashMap<>();

    @NotNull
    public JSONAPIClass resolve(String name) {
        JSONAPIClass result = cache.get(name);
        if (result == null) {
            result = classes.get(name);
            if (result == null) {
                if (name.endsWith("[]")) {
                    String docName = name.substring(0, name.length() - 2);
                    if (resolve(docName).hasOwnDocumentation()) {
                        result = new JSONAPIClass(name, true, docName);
                    }
                }
                if (result == null) {
                    result = new JSONAPIClass(name, false);
                }
                cache.put(name, result);
            } else {
                cache.put(name, result);
            }
        }
        return result;
    }

    public boolean contains(String name) {
        return classes.containsKey(name);
    }

    public void register(String alias, JSONAPIClass clazz) {
        this.classes.put(alias, clazz);
    }

    public void register(JSONAPIClass clazz) {
        this.classes.put(clazz.getName(), clazz);
    }
}
