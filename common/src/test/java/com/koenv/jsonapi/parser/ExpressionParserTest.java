package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ExpressionParserTest {
    @Test
    public void oneMethod() throws Exception {
        new ExpressionParser().parse("getIt()");
    }

    @Test
    public void chainedMethods() throws Exception {
        new ExpressionParser().parse("getIt().getIt()");
    }

    @Test
    public void integerAsParameter() throws Exception {
        new ExpressionParser().parse("getIt(12)");
    }

    @Test
    public void stringAsParameter() throws Exception {
        new ExpressionParser().parse("getIt(\"test\")");
    }

    @Test
    public void doubleAsParameter() throws Exception {
        new ExpressionParser().parse("getIt(12.67)");
    }

    @Test
    public void booleanAsParameter() throws Exception{
        new ExpressionParser().parse("getIt(true)");
    }

    @Test
    public void methodAsParameter() throws Exception {
        new ExpressionParser().parse("getIt(getIt())");
    }

    @Test
    public void methodWithParametersAsParameter() throws Exception {
        new ExpressionParser().parse("getIt(getThat(12, \"test\"))");
    }

    @Test
    public void simpleNamespace() throws Exception {
        new ExpressionParser().parse("test.getThat()");
    }

    @Test
    public void multipleNamespaces() throws Exception {
        new ExpressionParser().parse("test.getThat(test.getThat())");
    }

    @Test
    public void methodWithManyDifferentParameters() throws Exception {
        new ExpressionParser().parse("test.getThat(test.getThat(12, \"get\"), 67.23)");
    }

    @Test
    public void methodWithParametersAsParameterAndIntegerAsParameter() throws Exception {
        Expression expression = new ExpressionParser().parse("it.getIt(getThat(12, \"test\"), 12)");

        assertThat(expression, instanceOf(ChainedMethodCallExpression.class));

        List<Expression> expressions = ((ChainedMethodCallExpression) expression).getExpressions();

        assertEquals(2, expressions.size());
        assertThat(expressions.get(0), instanceOf(NamespaceExpression.class));
        assertThat(expressions.get(1), instanceOf(MethodCallExpression.class));

        NamespaceExpression firstNamespace = (NamespaceExpression) expressions.get(0);
        assertEquals("it", firstNamespace.getName());

        MethodCallExpression firstMethodCall = (MethodCallExpression) expressions.get(1);
        assertEquals("getIt", firstMethodCall.getMethodName());
        assertEquals(2, firstMethodCall.getParameters().size());
        assertThat(firstMethodCall.getParameters().get(0), instanceOf(ChainedMethodCallExpression.class));
        assertThat(firstMethodCall.getParameters().get(1), instanceOf(IntegerExpression.class));

        ChainedMethodCallExpression secondChainedMethodCall = (ChainedMethodCallExpression) firstMethodCall.getParameters().get(0);
        assertThat(secondChainedMethodCall.getExpressions().get(0), instanceOf(MethodCallExpression.class));

        MethodCallExpression secondMethodCall = (MethodCallExpression) secondChainedMethodCall.getExpressions().get(0);
        assertEquals("getThat", secondMethodCall.getMethodName());
        assertEquals(2, secondMethodCall.getParameters().size());
        assertThat(secondMethodCall.getParameters().get(0), instanceOf(IntegerExpression.class));
        assertThat(secondMethodCall.getParameters().get(1), instanceOf(StringExpression.class));

        IntegerExpression secondMethodCallParameter1 = (IntegerExpression) secondMethodCall.getParameters().get(0);
        assertEquals(12, (int) secondMethodCallParameter1.getValue());

        StringExpression secondMethodCallParameter2 = (StringExpression) secondMethodCall.getParameters().get(1);
        assertEquals("test", secondMethodCallParameter2.getValue());

        IntegerExpression firstMethodCallParameter2 = (IntegerExpression) firstMethodCall.getParameters().get(1);
        assertEquals(12, (int) firstMethodCallParameter2.getValue());
    }

    @Test(expected = ParseException.class)
    public void constantAsParameterThrowsParseException() throws Exception {
        new ExpressionParser().parse("getIt(name)");
    }

    @Test(expected = ParseException.class)
    public void invalidMethodThrowsParseException() throws Exception {
        new ExpressionParser().parse("namespace.getIt(name)as");
    }

    @Test(expected = ParseException.class)
    public void invalidNumberExpressionThrowsParseException() throws Exception {
        new ExpressionParser().parse("getIt().12.67");
    }

    @Test(expected = ParseException.class)
    public void invalidNumberOfParenthesesThrowsParseException() throws Exception {
        new ExpressionParser().parse("getIt(getIt()");
    }

    @Test(expected = ParseException.class)
    public void invalidNamespacesThrowsParseException() throws Exception {
        new ExpressionParser().parse("test.getThat().test.getThat()");
    }
}
