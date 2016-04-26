package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.StringJoiner;

public class ClassMethod extends AbstractV1Method {
    private String operatesOn;

    public ClassMethod(JSONObject jsonObject) {
        super(jsonObject);
        this.populateFrom(jsonObject);
    }

    public String getOperatesOn() {
        return operatesOn;
    }

    @Override
    public void populateFrom(JSONObject jsonObject) {
        super.populateFrom(jsonObject);
        this.populateRestFrom(jsonObject);
    }

    @Override
    public String getDeclaration() {
        return operatesOn +
                "." +
                getDeclarationWithoutOperatesOn();
    }

    public String getDeclarationWithoutOperatesOn() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append("(");
        StringJoiner joiner = new StringJoiner(", ");

        arguments
                .stream()
                .skip(1)
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

    private void populateRestFrom(JSONObject jsonObject) {
        this.operatesOn = jsonObject.getString("operatesOn");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassMethod)) return false;
        if (!super.equals(o)) return false;

        ClassMethod that = (ClassMethod) o;

        return operatesOn != null ? operatesOn.equals(that.operatesOn) : that.operatesOn == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (operatesOn != null ? operatesOn.hashCode() : 0);
        return result;
    }
}
