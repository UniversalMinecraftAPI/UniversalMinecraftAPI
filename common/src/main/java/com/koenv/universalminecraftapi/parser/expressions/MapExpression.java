package com.koenv.universalminecraftapi.parser.expressions;

import java.util.Map;

/**
 * A map expression, such as `{"key" = "value"}` (in this example having two string expressions)
 */
public class MapExpression extends ValueExpression {
    private Map<Expression, Expression> value;

    public MapExpression(Map<Expression, Expression> value) {
        this.value = value;
    }

    @Override
    public Map<Expression, Expression> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapExpression that = (MapExpression) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MapExpression{" +
                "value=" + value +
                '}';
    }
}
