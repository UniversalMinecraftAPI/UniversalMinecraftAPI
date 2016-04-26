package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONObject;

public class RestOperation extends AbstractV2Method {
    private String operatesOn;

    public RestOperation(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getOperatesOn() {
        return operatesOn;
    }

    @Override
    protected void populateRestFrom(JSONObject jsonObject) {
        this.operatesOn = jsonObject.getString("operatesOn");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RestOperation that = (RestOperation) o;

        return operatesOn != null ? operatesOn.equals(that.operatesOn) : that.operatesOn == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (operatesOn != null ? operatesOn.hashCode() : 0);
        return result;
    }
}
