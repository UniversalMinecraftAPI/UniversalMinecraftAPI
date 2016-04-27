package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.StringJoiner;

public class NamespacedMethod extends AbstractV1Method {
    private String namespace;

    public NamespacedMethod(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getDeclaration() {
        return namespace + "." + getDeclarationWithoutNamespace();
    }

    public String getDeclarationWithoutNamespace() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append("(");
        StringJoiner joiner = new StringJoiner(", ");

        arguments
                .stream()
                .forEach(parameter -> {
                    String value = parameter.getName();
                    if (parameter.isOptional()) {
                        value = "[" + value + "]";
                    }
                    joiner.add(value);
                });

        stringBuilder.append(joiner.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    protected void populateRestFrom(JSONObject jsonObject) {
        this.namespace = jsonObject.getString("namespace");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespacedMethod)) return false;
        if (!super.equals(o)) return false;

        NamespacedMethod that = (NamespacedMethod) o;

        return namespace != null ? namespace.equals(that.namespace) : that.namespace == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }
}
