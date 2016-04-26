package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.http.rest.RestMethod;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.List;

public class RestOperation extends AbstractV2Method {
    private String operatesOn;
    private String path;
    private RestMethod method;
    private List<?> bodyParams;

    public RestOperation(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getOperatesOn() {
        return operatesOn;
    }

    public String getPath() {
        return path;
    }

    public RestMethod getMethod() {
        return method;
    }

    public List<?> getBodyParams() {
        return bodyParams;
    }

    @Override
    protected void populateRestFrom(JSONObject jsonObject) {
        this.operatesOn = jsonObject.getString("operatesOn");
        this.path = jsonObject.getString("path");
        this.method = RestMethod.valueOf(jsonObject.getString("method"));
        this.bodyParams = readArguments(jsonObject.getJSONArray("bodyParams"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RestOperation that = (RestOperation) o;

        if (operatesOn != null ? !operatesOn.equals(that.operatesOn) : that.operatesOn != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (method != that.method) return false;
        return bodyParams != null ? bodyParams.equals(that.bodyParams) : that.bodyParams == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (operatesOn != null ? operatesOn.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (bodyParams != null ? bodyParams.hashCode() : 0);
        return result;
    }
}
