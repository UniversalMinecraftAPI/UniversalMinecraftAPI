package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractV2Method {
    protected List<V2Argument> arguments;
    protected String returns;

    public AbstractV2Method(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public void populateFrom(JSONObject jsonObject) {
        this.arguments = new ArrayList<>();
        JSONArray jsonArguments = jsonObject.getJSONArray("arguments");
        for (int i = 0; i < jsonArguments.length(); i++) {
            arguments.add(new V2Argument(jsonArguments.getJSONObject(i)));
        }

        this.returns = jsonObject.getString("returns");

        this.populateRestFrom(jsonObject);
    }

    protected abstract void populateRestFrom(JSONObject jsonObject);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractV2Method that = (AbstractV2Method) o;

        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        return returns != null ? returns.equals(that.returns) : that.returns == null;

    }

    @Override
    public int hashCode() {
        int result = arguments != null ? arguments.hashCode() : 0;
        result = 31 * result + (returns != null ? returns.hashCode() : 0);
        return result;
    }
}
