package com.koenv.universalminecraftapi.docgenerator.resolvers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.koenv.universalminecraftapi.docgenerator.model.AbstractMethod;
import com.koenv.universalminecraftapi.docgenerator.model.Platform;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PlatformResolver {
    private Set<Platform> platforms = new HashSet<>();

    private Multimap<AbstractMethod, Platform> methods = HashMultimap.create();
    private Multimap<String, Platform> streams = HashMultimap.create();

    public void addMethod(Platform platform, AbstractMethod method) {
        this.platforms.add(platform);
        this.methods.put(method, platform);
    }

    public void addStream(Platform platform, String stream) {
        this.platforms.add(platform);
        this.streams.put(stream, platform);
    }

    public boolean availableOnAllPlatforms(AbstractMethod method) {
        return methods.get(method).size() == platforms.size();
    }

    public Collection<Platform> getPlatforms(AbstractMethod method) {
        return methods.get(method);
    }

    public boolean availableOnAllPlatforms(String stream) {
        return streams.get(stream).size() == platforms.size();
    }

    public Collection<Platform> getPlatforms(String stream) {
        return streams.get(stream);
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }
}
