package com.koenv.jsonapi.util;

/**
 * A simple counter so it can be passed as a reference.
 */
public class Counter {
    private int count = 0;

    /**
     * Increment the counter.
     */
    public void increment() {
        count++;
    }

    /**
     * Decrement the counter.
     */
    public void decrement() {
        count--;
    }

    /**
     * @return The current count
     */
    public int count() {
        return count;
    }
}
