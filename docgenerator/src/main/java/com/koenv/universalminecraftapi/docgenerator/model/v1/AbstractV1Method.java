package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractV1Method {
    protected static final Logger logger = LoggerFactory.getLogger("V1Method");

    protected String name;
    protected List<V1Argument> arguments;
    protected String returns;
    protected String permission;

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

    public String getPermission() {
        return permission;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");

        this.arguments = new ArrayList<>();
        JSONArray jsonArguments = jsonObject.getJSONArray("arguments");
        for (int i = 0; i < jsonArguments.length(); i++) {
            arguments.add(new V1Argument(jsonArguments.getJSONObject(i)));
        }

        this.returns = jsonObject.getString("returns");
        this.permission = jsonObject.getString("permission");

        this.populateRestFrom(jsonObject);

        if (this.permission.isEmpty()) {
            logger.warn("No permission set for method " + getDeclaration());
        }
    }

    protected abstract void populateRestFrom(JSONObject jsonObject);

    public abstract String getDeclaration();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractV1Method)) return false;

        AbstractV1Method that = (AbstractV1Method) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (returns != null ? !returns.equals(that.returns) : that.returns != null) return false;
        return permission != null ? permission.equals(that.permission) : that.permission == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (returns != null ? returns.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        return result;
    }
}
