package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class V2Argument {
    private String name;
    private String type;

    public V2Argument(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.type = jsonObject.getString("type");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        V2Argument that = (V2Argument) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
