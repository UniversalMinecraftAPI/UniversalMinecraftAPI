package com.koenv.universalminecraftapi.docgenerator.model;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class Platform {
    private String name;
    private String version;

    public Platform(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public Platform(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.version = jsonObject.getString("version");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;

        Platform platform = (Platform) o;

        if (name != null ? !name.equals(platform.name) : platform.name != null) return false;
        return version != null ? version.equals(platform.version) : platform.version == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
