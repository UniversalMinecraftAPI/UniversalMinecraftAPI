package com.koenv.jsonapi;

/**
 * The interface an implementation must implement, so it can be called by the {@link JSONAPIInterface}
 */
public interface JSONAPIProvider {
    void reloadUsers() throws Exception;

    String getJSONAPIVersion();
    String getPlatform();
    String getPlatformVersion();
}
