package com.koenv.universalminecraftapi.parser.expressions;

/**
 * An expression.
 */
public abstract class Expression {
    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
