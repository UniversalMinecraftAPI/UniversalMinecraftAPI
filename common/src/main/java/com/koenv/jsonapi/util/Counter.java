package com.koenv.jsonapi.util;

public class Counter {
    private int count = 0;

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public int count() {
        return count;
    }
}
