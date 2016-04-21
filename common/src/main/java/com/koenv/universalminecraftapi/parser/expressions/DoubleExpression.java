package com.koenv.universalminecraftapi.parser.expressions;

/**
 * Double expression, such as `12.67`.
 */
public class DoubleExpression extends NumberExpression {
    private double value;

    public DoubleExpression(double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleExpression that = (DoubleExpression) o;

        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return "DoubleExpression{" +
                "value=" + value +
                '}';
    }
}
