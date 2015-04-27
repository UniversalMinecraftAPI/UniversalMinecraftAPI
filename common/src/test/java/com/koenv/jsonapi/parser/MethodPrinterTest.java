package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MethodPrinterTest {
    @Test
    public void oneMethodPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        assertEquals("getIt()", MethodPrinter.printBlocks(expressions));
    }

    @Test
    public void complexMethodPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("this"));
        expressions.add(new MethodCallExpression("getPlayers", new ArrayList<>()));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new IntegerExpression(12));
        parameters.add(new StringExpression("name"));
        expressions.add(new MethodCallExpression("filterBy", parameters));

        assertEquals("this.getPlayers().filterBy(12, \"name\")", MethodPrinter.printBlocks(expressions));
    }

    @Test
    public void escapedStringPrint() throws Exception {
        assertEquals("Escape double quotes", "\"\"hey\"\"", MethodPrinter.printStringParameter(new StringExpression("\"hey\"")));

        assertEquals("Escape null byte", "\"\b0\"", MethodPrinter.printStringParameter(new StringExpression("\b0")));
    }
}
