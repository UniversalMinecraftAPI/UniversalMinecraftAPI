package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractV1Method {
    protected String name;
    protected List<V1Argument> arguments;
    protected String returns;

    public AbstractV1Method(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public String getName() {
        return name;
    }

    public List<V1Argument> getArguments() {
        return arguments;
    }

    public String getReturns() {
        return returns;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");

        this.arguments = new ArrayList<>();
        JSONArray jsonArguments = jsonObject.getJSONArray("arguments");
        for (int i = 0; i < jsonArguments.length(); i++) {
            arguments.add(new V1Argument(jsonArguments.getJSONObject(i)));
        }

        this.returns = jsonObject.getString("returns");
    }

    public abstract String getDeclaration();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractV1Method)) return false;

        AbstractV1Method that = (AbstractV1Method) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        return returns != null ? returns.equals(that.returns) : that.returns == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (returns != null ? returns.hashCode() : 0);
        return result;
    }
}
