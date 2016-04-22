package com.koenv.universalminecraftapi.parser;

import com.koenv.universalminecraftapi.util.Counter;

public class ParseContext {
    private Counter depth = new Counter();
    private Counter parenthesesCounter = new Counter();
    private Counter bracesCounter = new Counter();
    private Counter bracketsCounter = new Counter();

    /**
     * @return The counter for the depth
     */
    public Counter getDepth() {
        return depth;
    }

    /**
     * @return The counter for the number of parentheses (`(` and `)`)
     */
    public Counter getParenthesesCounter() {
        return parenthesesCounter;
    }

    /**
     * @return The counter for the number of braces (`{` and `}`)
     */
    public Counter getBracesCounter() {
        return bracesCounter;
    }

    /**
     * @return The counter for the number of brackets (`[` and `]`)
     */
    public Counter getBracketsCounter() {
        return bracketsCounter;
    }
}
