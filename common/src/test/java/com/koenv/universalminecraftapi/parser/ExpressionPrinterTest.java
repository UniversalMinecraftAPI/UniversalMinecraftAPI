package com.koenv.universalminecraftapi.parser;

import com.koenv.universalminecraftapi.parser.expressions.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ExpressionPrinterTest {
    @Test
    public void oneMethodPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        assertEquals("getIt()", ExpressionPrinter.printExpressions(expressions));
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

        assertEquals("this.getPlayers().filterBy(12, \"name\")", ExpressionPrinter.printExpressions(expressions));
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

        assertEquals("ints.getInt(objects.getObjectExtension())", ExpressionPrinter.printExpressions(expressions));
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

        assertEquals("test.getThat(test.getThat(12, \"get\"), 67.23)", ExpressionPrinter.printExpressions(expressions));
    }

    @Test
    public void escapedStringPrint() throws Exception {
        assertEquals("Escape double quotes", "\"\"hey\"\"", ExpressionPrinter.printStringExpression(new StringExpression("\"hey\"")));

        assertEquals("Escape null byte", "\"\b0\"", ExpressionPrinter.printStringExpression(new StringExpression("\b0")));
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

        assertEquals("getIt(12, getIt())", ExpressionPrinter.printExpressions(expressions));
    }

    @Test
    public void nestedMapPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();

        Map<Expression, Expression> map = new HashMap<>();
        Map<Expression, Expression> nestedMap = new HashMap<>();
        nestedMap.put(new StringExpression("key"), new MethodCallExpression("getIt", new ArrayList<>()));
        nestedMap.put(new IntegerExpression(12), new StringExpression("value"));

        map.put(new StringExpression("map"), new MapExpression(nestedMap));
        map.put(new StringExpression("double"), new DoubleExpression(12.67));

        expressions.add(new MethodCallExpression("getIt", Arrays.asList(new MapExpression(map), new DoubleExpression(12.67))));

        assertEquals("getIt({\"double\" = 12.67, \"map\" = {12 = \"value\", \"key\" = getIt()}}, 12.67)", ExpressionPrinter.printExpressions(expressions));
    }

    @Test
    public void nestedListPrint() throws Exception {
        List<Expression> expressions = new ArrayList<>();

        List< Expression> list = new ArrayList<>();
        List<Expression> nestedList = new ArrayList<>();
        nestedList.add(new MethodCallExpression("getIt", new ArrayList<>()));
        nestedList.add(new IntegerExpression(12));

        list.add(new ListExpression(nestedList));
        list.add(new StringExpression("double"));

        expressions.add(new MethodCallExpression("getIt", Arrays.asList(new ListExpression(list), new DoubleExpression(12.67))));

        assertEquals("getIt([[getIt(), 12], \"double\"], 12.67)", ExpressionPrinter.printExpressions(expressions));
    }
}
