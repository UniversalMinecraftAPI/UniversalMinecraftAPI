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
        assertEquals(12, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeMethodWithNamespace() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("getThat", new ArrayList<>()));

        // test.getThat()
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test
    public void invokeNestedMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new MethodCallExpression("getIt", new ArrayList<>()));

        expressions.add(new MethodCallExpression("getInt", parameters));

        // ints.getInt(getIt())
        assertEquals(12, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeChainedMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(new MethodCallExpression("getObject", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getInt", new ArrayList<>()));

        // objects.getObject().getInt()
        assertEquals(18, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void convertParameter() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new StringExpression("baa3d81b-0b77-47dd-ac48-0371098d373d"));
        expressions.add(new MethodCallExpression("getUUID", parameters));

        // objects.getUUID("baa3d81b-0b77-47dd-ac48-0371098d373d")
        assertEquals("baa3d81b-0b77-47dd-ac48-0371098d373d", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void convertParameterRecursively() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        List<Expression> parameters = new ArrayList<>();
        List<Expression> chainedMethodCall = new ArrayList<>();
        chainedMethodCall.add(new NamespaceExpression("objects"));
        chainedMethodCall.add(new MethodCallExpression("getObjectExtension", new ArrayList<>()));
        parameters.add(new ChainedMethodCallExpression(chainedMethodCall));
        expressions.add(new MethodCallExpression("getInt", parameters));

        // ints.getInt(objects.getObjectExtension())
        assertEquals(19, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testLong() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("longs"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new MethodCallExpression("getIt", new ArrayList<>()));

        expressions.add(new MethodCallExpression("getLong", parameters));

        // longs.getLong(getIt())
        assertEquals(12L, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testFloat() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("floats"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new DoubleExpression(12.67));

        expressions.add(new MethodCallExpression("getFloat", parameters));

        // floats.getFloat(12.67)
        assertEquals(12.67f, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testBoolean() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("booleans"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new BooleanExpression(true));

        expressions.add(new MethodCallExpression("getBoolean", parameters));

        // booleans.getBoolean(true)
        assertEquals(true, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test(expected = MethodInvocationException.class)
    public void invokeMethodWithWrongIntParameter() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        List<Expression> parameters = new ArrayList<>();
        parameters.add(new DoubleExpression(12.67));
        expressions.add(new MethodCallExpression("getInt", parameters));

        // getInt(12.67)
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test(expected = MethodInvocationException.class)
    public void namespaceNotInFirstPosition() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("testIt", new ArrayList<>()));

        // getIt().test.testIt()
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    private MethodInvoker buildMethodInvoker() {
        MethodInvoker methodInvoker = new MethodInvoker();
        methodInvoker.registerMethods(new MethodInvokerTestClass());
        methodInvoker.registerParameterConverter(String.class, UUID.class, new ParameterConverter<String, UUID>() {
            @Override
            public UUID convert(String s) {
                return UUID.fromString(s);
            }
        });
        methodInvoker.registerParameterConverter(MethodInvokerTestClass.MethodInvokerTestObject.class, int.class, new ParameterConverter<MethodInvokerTestClass.MethodInvokerTestObject, Integer>() {

            @Override
            public Integer convert(MethodInvokerTestClass.MethodInvokerTestObject methodInvokerTestObject) {
                return methodInvokerTestObject.getInt();
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

        @APIMethod(namespace = "longs")
        public static long getLong(long arg) {
            return arg;
        }

        @APIMethod(namespace = "floats")
        public static float getFloat(float arg) {
            return arg;
        }

        @APIMethod(namespace = "booleans")
        public static boolean getBoolean(boolean arg) {
            return arg;
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObject getObject() {
            return new MethodInvokerTestObject();
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObjectExtension getObjectExtension() {
            return new MethodInvokerTestObjectExtension();
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

        private static class MethodInvokerTestObjectExtension extends MethodInvokerTestObject {
            public int getInt() {
                return 19;
            }
        }
    }
}
