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

        assertEquals("getIt()", MethodPrinter.printExpressions(expressions));
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

        assertEquals("this.getPlayers().filterBy(12, \"name\")", MethodPrinter.printExpressions(expressions));
    }

    @Test
    public void recursiveMethodPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        List<Expression> parameters = new ArrayList<>();
        List<Expression> chainedMethodCall = new ArrayList<>();
        chainedMethodCall.add(new NamespaceExpression("objects"));
        chainedMethodCall.add(new MethodCallExpression("getObjectExtension", new ArrayList<>()));
        parameters.add(new ChainedMethodCallExpression(chainedMethodCall));
        expressions.add(new MethodCallExpression("getInt", parameters));

        assertEquals("ints.getInt(objects.getObjectExtension())", MethodPrinter.printExpressions(expressions));
    }

    @Test
    public void methodWithManyDifferentParametersPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("test"));

        List<Expression> firstParameters = new ArrayList<>();

        List<Expression> secondParameters = new ArrayList<>();
        secondParameters.add(new IntegerExpression(12));
        secondParameters.add(new StringExpression("get"));

        List<Expression> firstParameter = new ArrayList<>();
        firstParameter.add(new NamespaceExpression("test"));
        firstParameter.add(new MethodCallExpression("getThat", secondParameters));

        firstParameters.add(new ChainedMethodCallExpression(firstParameter));
        firstParameters.add(new DoubleExpression(67.23));

        expressions.add(new MethodCallExpression("getThat", firstParameters));

        assertEquals("test.getThat(test.getThat(12, \"get\"), 67.23)", MethodPrinter.printExpressions(expressions));
    }

    @Test
    public void escapedStringPrint() throws Exception {
        assertEquals("Escape double quotes", "\"\"hey\"\"", MethodPrinter.printStringExpression(new StringExpression("\"hey\"")));

        assertEquals("Escape null byte", "\"\b0\"", MethodPrinter.printStringExpression(new StringExpression("\b0")));
    }

    @Test
    public void nestedMethodPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        List<Expression> parameters = new ArrayList<>();
        parameters.add(new IntegerExpression(12));
        List<Expression> chainedExpressions = new ArrayList<>();
        chainedExpressions.add(new MethodCallExpression("getIt", new ArrayList<>()));
        parameters.add(new ChainedMethodCallExpression(chainedExpressions));
        expressions.add(new MethodCallExpression("getIt", parameters));

        assertEquals("getIt(12, getIt())", MethodPrinter.printExpressions(expressions));
    }
}
