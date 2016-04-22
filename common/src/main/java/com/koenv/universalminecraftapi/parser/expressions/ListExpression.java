package com.koenv.universalminecraftapi.parser.expressions;

import java.util.List;

public class ListExpression extends ValueExpression {
    private List<Expression> value;

    public ListExpression(List<Expression> value) {
        this.value = value;
    }

    @Override
    public List<Expression> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListExpression that = (ListExpression) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ListExpression{" +
                "value=" + value +
                '}';
    }
}
