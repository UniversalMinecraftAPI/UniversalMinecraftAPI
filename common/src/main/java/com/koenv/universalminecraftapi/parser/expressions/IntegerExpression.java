package com.koenv.universalminecraftapi.parser.expressions;

/**
 * An integer expression, such as `21`.
 */
public class IntegerExpression extends NumberExpression {
    private int value;

    public IntegerExpression(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerExpression that = (IntegerExpression) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerExpression{" +
                "value=" + value +
                '}';
    }
}
