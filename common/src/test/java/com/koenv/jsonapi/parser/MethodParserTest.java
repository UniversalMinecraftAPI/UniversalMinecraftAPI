package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.expressions.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MethodParserTest {
    @Test
    public void oneMethod() throws Exception {
        new MethodParser().parse("getIt()");
    }

    @Test
    public void chainedMethods() throws Exception {
        new MethodParser().parse("getIt().getIt()");
    }

    @Test
    public void integerAsParameter() throws Exception {
        new MethodParser().parse("getIt(12)");
    }

    @Test
    public void stringAsParameter() throws Exception {
        new MethodParser().parse("getIt(\"test\")");
    }

    @Test
    public void doubleAsParameter() throws Exception {
        new MethodParser().parse("getIt(12.67)");
    }

    @Test
    public void methodAsParameter() throws Exception {
        new MethodParser().parse("getIt(getIt())");
    }

    @Test
    public void methodWithParametersAsParameter() throws Exception {
        new MethodParser().parse("getIt(getThat(12, \"test\"))");
    }

    @Test
    public void simpleNamespace() throws Exception {
        new MethodParser().parse("test.getThat()");
    }

    @Test
    public void multipleNamespaces() throws Exception {
        new MethodParser().parse("test.getThat(test.getThat())");
    }

    @Test
    public void methodWithParametersAsParameterAndIntegerAsParameter() throws Exception {
        Expression expression = new MethodParser().parse("it.getIt(getThat(12, \"test\"), 12)");
        assertThat(expression, instanceOf(ChainedMethodCallExpression.class));

        ChainedMethodCallExpression root = (ChainedMethodCallExpression) expression;
        assertEquals(2, root.getExpressions().size());
        assertThat(root.getExpressions().get(0), instanceOf(NamespaceExpression.class));
        assertThat(root.getExpressions().get(1), instanceOf(MethodCallExpression.class));

        NamespaceExpression firstNamespace = (NamespaceExpression) root.getExpressions().get(0);
        assertEquals("it", firstNamespace.getName());

        MethodCallExpression firstMethodCall = (MethodCallExpression) root.getExpressions().get(1);
        assertEquals("getIt", firstMethodCall.getMethodName());
        assertEquals(2, firstMethodCall.getParameters().size());
        assertThat(firstMethodCall.getParameters().get(0), instanceOf(MethodCallExpression.class));
        assertThat(firstMethodCall.getParameters().get(1), instanceOf(IntegerExpression.class));

        MethodCallExpression secondMethodCall = (MethodCallExpression) firstMethodCall.getParameters().get(0);
        assertEquals("getThat", secondMethodCall.getMethodName());
        assertEquals(2, secondMethodCall.getParameters().size());
        assertThat(secondMethodCall.getParameters().get(0), instanceOf(IntegerExpression.class));
        assertThat(secondMethodCall.getParameters().get(1), instanceOf(StringExpression.class));

        IntegerExpression secondMethodCallParameter1 = (IntegerExpression) secondMethodCall.getParameters().get(0);
        assertEquals(12, secondMethodCallParameter1.getValue());

        StringExpression secondMethodCallParameter2 = (StringExpression) secondMethodCall.getParameters().get(1);
        assertEquals("test", secondMethodCallParameter2.getValue());

        IntegerExpression firstMethodCallParameter2 = (IntegerExpression) firstMethodCall.getParameters().get(1);
        assertEquals(12, firstMethodCallParameter2.getValue());
    }

    @Test(expected = ParseException.class)
    public void constantAsParameterThrowsParseException() throws Exception {
        new MethodParser().parse("getIt(name)");
    }

    @Test(expected = ParseException.class)
    public void invalidMethodThrowsParseException() throws Exception {
        new MethodParser().parse("namespace.getIt(name)as");
    }

    @Test(expected = ParseException.class)
    public void invalidNumberExpressionThrowsParseException() throws Exception {
        new MethodParser().parse("getIt().12.67");
    }

    @Test(expected = ParseException.class)
    public void invalidNumberOfParenthesesThrowsParseException() throws Exception {
        new MethodParser().parse("getIt(getIt()");
    }

    @Test(expected = ParseException.class)
    public void invalidNamespacesThrowsParseException() throws Exception {
        new MethodParser().parse("test.getThat().test.getThat()");
    }
}
