package com.koenv.universalminecraftapi.docgenerator.resolvers;

import com.koenv.universalminecraftapi.docgenerator.model.UniversalMinecraftAPIClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClassResolver {
    private Map<String, UniversalMinecraftAPIClass> cache = new HashMap<>();
    private Map<String, UniversalMinecraftAPIClass> classes = new HashMap<>();

    public @NotNull UniversalMinecraftAPIClass resolve(String name) {
        UniversalMinecraftAPIClass result = cache.get(name);
        if (result == null) {
            result = classes.get(name);
            if (result == null) {
                if (name.endsWith("[]")) {
                    String docName = name.substring(0, name.length() - 2);
                    if (resolve(docName).hasOwnDocumentation()) {
                        result = new UniversalMinecraftAPIClass(name, true, docName);
                    }
                }
                if (result == null) {
                    result = new UniversalMinecraftAPIClass(name, false);
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

    public void register(String alias, UniversalMinecraftAPIClass clazz) {
        this.classes.put(alias, clazz);
    }

    public void register(UniversalMinecraftAPIClass clazz) {
        this.classes.put(clazz.getName(), clazz);
    }
}
