package com.koenv.universalminecraftapi;

/**
 * The interface an implementation must implement, so it can be called by the {@link UniversalMinecraftAPIInterface}
 */
public interface UniversalMinecraftAPIProvider {
    void reloadUsers() throws Exception;

    String getUMAVersion();
    String getPlatform();
    String getPlatformVersion();
}
