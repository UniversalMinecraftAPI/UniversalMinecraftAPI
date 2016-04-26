package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class V1Argument {
    private String name;
    private String type;
    private boolean optional;

    public V1Argument(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.type = jsonObject.getString("type");
        this.optional = jsonObject.getBoolean("optional");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof V1Argument)) return false;

        V1Argument argument = (V1Argument) o;

        if (name != null ? !name.equals(argument.name) : argument.name != null) return false;
        return type != null ? type.equals(argument.type) : argument.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
