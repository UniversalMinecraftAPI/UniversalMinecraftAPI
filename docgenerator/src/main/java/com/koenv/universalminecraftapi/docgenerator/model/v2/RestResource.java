package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class RestResource extends AbstractV2Method {
    private String path;

    public RestResource(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected void populateRestFrom(JSONObject jsonObject) {
        this.path = jsonObject.getString("path");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RestResource that = (RestResource) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
