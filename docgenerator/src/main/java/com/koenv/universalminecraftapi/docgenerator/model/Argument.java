package com.koenv.universalminecraftapi.docgenerator.model;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class Argument {
    private String name;
    private String type;
    private boolean optional;

    public Argument(JSONObject jsonObject) {
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
        if (!(o instanceof Argument)) return false;

        Argument argument = (Argument) o;

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
