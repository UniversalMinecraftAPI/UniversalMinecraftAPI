package com.koenv.universalminecraftapi.parser.expressions;

import java.util.List;

/**
 * A method call expression, such as `getPlayer("Koen")`.
 */
public class MethodCallExpression extends Expression {
    private String methodName;
    private List<Expression> parameters;

    public MethodCallExpression(String methodName, List<Expression> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodCallExpression that = (MethodCallExpression) o;

        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        return parameters != null ? parameters.equals(that.parameters) : that.parameters == null;

    }

    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodCallExpression{" +
                "methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
