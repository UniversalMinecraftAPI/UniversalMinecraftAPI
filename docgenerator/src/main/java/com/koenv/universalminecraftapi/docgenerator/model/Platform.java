package com.koenv.universalminecraftapi.docgenerator.model;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class Platform {
    private String name;
    private String rawName;
    private String version;
    private String umaVersion;

    public Platform(String name, JSONObject jsonObject) {
        this.name = name;
        this.populateFrom(jsonObject);
    }

    public String getName() {
        return name;
    }

    public String getRawName() {
        return rawName;
    }

    public String getVersion() {
        return version;
    }

    public String getUmaVersion() {
        return umaVersion;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.rawName = jsonObject.getString("name");
        this.version = jsonObject.getString("version");
        this.umaVersion = jsonObject.getString("umaVersion");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;

        Platform platform = (Platform) o;

        if (name != null ? !name.equals(platform.name) : platform.name != null) return false;
        if (rawName != null ? !rawName.equals(platform.rawName) : platform.rawName != null) return false;
        if (version != null ? !version.equals(platform.version) : platform.version != null) return false;
        return umaVersion != null ? umaVersion.equals(platform.umaVersion) : platform.umaVersion == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (rawName != null ? rawName.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (umaVersion != null ? umaVersion.hashCode() : 0);
        return result;
    }
}
