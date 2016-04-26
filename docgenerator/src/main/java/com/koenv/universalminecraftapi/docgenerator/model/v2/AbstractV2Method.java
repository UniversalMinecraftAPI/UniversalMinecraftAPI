package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.docgenerator.model.v1.V1Argument;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractV2Method {
    protected String returns;

    public AbstractV2Method(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public void populateFrom(JSONObject jsonObject) {
        this.returns = jsonObject.getString("returns");

        this.populateRestFrom(jsonObject);
    }

    protected abstract void populateRestFrom(JSONObject jsonObject);

    protected List<V2Argument> readArguments(JSONArray jsonArray) {
        List<V2Argument> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(new V2Argument(jsonArray.getJSONObject(i)));
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractV2Method)) return false;

        AbstractV2Method that = (AbstractV2Method) o;

        return returns != null ? returns.equals(that.returns) : that.returns == null;

    }

    @Override
    public int hashCode() {
        return returns != null ? returns.hashCode() : 0;
    }
}
