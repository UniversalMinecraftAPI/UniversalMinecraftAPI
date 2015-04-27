package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.methods.exception.MethodInvocationException;
import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.parser.expressions.MethodCallExpression;
import com.koenv.jsonapi.parser.expressions.NamespaceExpression;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MethodInvokerTest {
    @Test
    public void invokeMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        buildMethodInvoker().invokeMethod(expressions);
    }

    @Test
    public void namespaceNotInFirstPosition() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("testIt", new ArrayList<>()));

        try {
            buildMethodInvoker().invokeMethod(expressions);
            fail("Expected MethodInvocationException to be thrown");
        } catch (MethodInvocationException e) {
            assertEquals("Namespace can only be in first position", e.getMessage());
        }
    }

    private MethodInvoker buildMethodInvoker() {
        MethodInvoker methodInvoker = new MethodInvoker();
        methodInvoker.registerMethods(new Object() {
            @APIMethod()
            public void getIt() {

            }
        });

        return methodInvoker;
    }
}
