package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.List;

public class RestResource extends AbstractV2Method {
    private String path;
    private List<?> pathParams;
    private List<?> queryParams;

    public RestResource(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getPath() {
        return path;
    }

    public List<?> getPathParams() {
        return pathParams;
    }

    public List<?> getQueryParams() {
        return queryParams;
    }

    @Override
    protected void populateRestFrom(JSONObject jsonObject) {
        this.path = jsonObject.getString("path");
        this.pathParams = readArguments(jsonObject.getJSONArray("pathParams"));
        this.queryParams = readArguments(jsonObject.getJSONArray("queryParams"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RestResource that = (RestResource) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (pathParams != null ? !pathParams.equals(that.pathParams) : that.pathParams != null) return false;
        return queryParams != null ? queryParams.equals(that.queryParams) : that.queryParams == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (pathParams != null ? pathParams.hashCode() : 0);
        result = 31 * result + (queryParams != null ? queryParams.hashCode() : 0);
        return result;
    }
}
