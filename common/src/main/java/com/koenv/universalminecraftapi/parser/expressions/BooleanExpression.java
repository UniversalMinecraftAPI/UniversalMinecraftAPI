package com.koenv.universalminecraftapi.parser.expressions;

/**
 * Boolean expression, such as `true` or `false`.
 */
public class BooleanExpression extends ValueExpression {
    private boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanExpression that = (BooleanExpression) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public String toString() {
        return "BooleanExpression{" +
                "value=" + value +
                '}';
    }
}
