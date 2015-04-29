package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.parser.expressions.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MethodInvokerTest {
    @Test
    public void invokeMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        // getIt()
        assertEquals(12, buildMethodInvoker().invokeMethod(expressions));
    }

    @Test
    public void invokeMethodWithNamespace() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("getThat", new ArrayList<>()));

        // test.getThat()
        buildMethodInvoker().invokeMethod(expressions);
    }

    @Test
    public void invokeNestedMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new MethodCallExpression("getIt", new ArrayList<>()));

        expressions.add(new MethodCallExpression("getInt", parameters));

        // ints.getInt(getIt())
        assertEquals(12, buildMethodInvoker().invokeMethod(expressions));
    }

    @Test
    public void invokeChainedMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(new MethodCallExpression("getObject", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getInt", new ArrayList<>()));

        // objects.getObject().getInt()
        assertEquals(18, buildMethodInvoker().invokeMethod(expressions));
    }

    @Test
    public void convertParameter() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new StringExpression("baa3d81b-0b77-47dd-ac48-0371098d373d"));
        expressions.add(new MethodCallExpression("getUUID", parameters));

        // objects.getUUID("baa3d81b-0b77-47dd-ac48-0371098d373d")
        assertEquals("baa3d81b-0b77-47dd-ac48-0371098d373d", buildMethodInvoker().invokeMethod(expressions));
    }

    @Test(expected = MethodInvocationException.class)
    public void invokeMethodWithWrongIntParameter() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        List<Expression> parameters = new ArrayList<>();
        parameters.add(new DoubleExpression(12.67));
        expressions.add(new MethodCallExpression("getInt", parameters));

        // getInt(12.67)
        buildMethodInvoker().invokeMethod(expressions);
    }

    @Test(expected = MethodInvocationException.class)
    public void namespaceNotInFirstPosition() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("testIt", new ArrayList<>()));

        // getIt().test.testIt()
        buildMethodInvoker().invokeMethod(expressions);
    }

    private MethodInvoker buildMethodInvoker() {
        MethodInvoker methodInvoker = new MethodInvoker();
        methodInvoker.registerMethods(new MethodInvokerTestClass());
        methodInvoker.registerParameterConvert(String.class, UUID.class, new ParameterConverter<String, UUID>() {
            @Override
            public UUID convert(String s) {
                return UUID.fromString(s);
            }
        });

        return methodInvoker;
    }

    private static class MethodInvokerTestClass {
        @APIMethod()
        public static int getIt() {
            return 12;
        }

        @APIMethod(namespace = "test")
        public static double getThat() {
            return 12.67;
        }

        @APIMethod(namespace = "ints")
        public static int getInt(int arg) {
            return arg;
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObject getObject() {
            return new MethodInvokerTestObject();
        }

        @APIMethod(namespace = "objects")
        public static String getUUID(UUID uuid) {
            return uuid.toString();
        }

        @APIMethod(operatesOn = MethodInvokerTestObject.class)
        public static int getInt(MethodInvokerTestObject methodInvokerTestObject) {
            return methodInvokerTestObject.getInt();
        }

        private static class MethodInvokerTestObject {
            public int getInt() {
                return 18;
            }
        }
    }
}
