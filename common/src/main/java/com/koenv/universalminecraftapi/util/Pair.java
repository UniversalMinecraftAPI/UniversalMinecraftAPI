package com.koenv.universalminecraftapi.util;

public class Pair<Left, Right> {
    private Left left;
    private Right right;

    public Pair(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public Left getLeft() {
        return left;
    }

    public Right getRight() {
        return right;
    }

    public static <Left, Right> Pair<Left, Right> of(Left left, Right right) {
        return new Pair<>(left, right);
    }
}
