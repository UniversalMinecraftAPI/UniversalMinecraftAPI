package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.parser.expressions.MethodCallExpression;
import com.koenv.jsonapi.parser.expressions.NamespaceExpression;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MethodInvokerTest {
    @Test
    public void invokeMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        buildMethodInvoker().invokeMethod(expressions);
    }

    @Test(expected = MethodInvocationException.class)
    public void namespaceNotInFirstPosition() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("testIt", new ArrayList<>()));

        buildMethodInvoker().invokeMethod(expressions);
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
