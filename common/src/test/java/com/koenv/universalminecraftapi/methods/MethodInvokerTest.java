package com.koenv.universalminecraftapi.methods;

import com.koenv.universalminecraftapi.http.model.APIException;
import com.koenv.universalminecraftapi.parser.expressions.*;
import com.koenv.universalminecraftapi.permissions.Permissible;
import com.koenv.universalminecraftapi.reflection.ParameterConverterManager;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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
    public void invokeChainedMethodOnParentClass() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(new MethodCallExpression("getObject", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getInt", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getName", new ArrayList<>()));

        // objects.getObject().getInt().getName()
        assertEquals("Integer", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeChainedMethodOnInterface() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("lists"));
        expressions.add(new MethodCallExpression("create", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getFirstItem", new ArrayList<>()));

        // lists.create().getFirstItem()
        assertEquals("test", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeChainedMethodWithNullIntermediateResult() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(new MethodCallExpression("getNull", new ArrayList<>()));
        expressions.add(new MethodCallExpression("getName", new ArrayList<>()));

        // objects.getNull().getName()
        assertEquals(null, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
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
    public void convertParameterRecursivelyInterface() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("booleans"));

        List<Expression> parameters = new ArrayList<>();
        List<Expression> chainedMethodCall = new ArrayList<>();
        chainedMethodCall.add(new NamespaceExpression("objects"));
        chainedMethodCall.add(new MethodCallExpression("getObjectExtension", new ArrayList<>()));
        parameters.add(new ChainedMethodCallExpression(chainedMethodCall));
        expressions.add(new MethodCallExpression("getBoolean", parameters));

        // booleans.getBoolean(objects.getObjectExtension())
        assertEquals(false, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test(expected = MethodInvocationException.class)
    public void invalidParameter() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        expressions.add(
                new MethodCallExpression(
                        "getInt",
                        Collections.singletonList(new StringExpression("test"))
                )
        );

        // ints.getInt(12)
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test(expected = MethodInvocationException.class)
    public void throwingMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("exceptions"));

        expressions.add(new MethodCallExpression("throwNormal", Collections.emptyList()));

        // exceptions.throwNormal()
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test(expected = RuntimeException.class)
    public void rethrowableMethod() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("exceptions"));

        expressions.add(new MethodCallExpression("throwRethrowable", Collections.emptyList()));

        // exceptions.throwRethrowable()
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
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

    @Test
    public void testMap() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("maps"));

        Map<Expression, Expression> map = new HashMap<>();
        map.put(new StringExpression("key"), new StringExpression("value"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new MapExpression(map));

        expressions.add(new MethodCallExpression("getMap", parameters));

        // maps.getMap({"key"="value"})
        assertEquals("value", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testNestedMap() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("maps"));

        Map<Expression, Expression> nestedMap = new HashMap<>();
        nestedMap.put(new StringExpression("key"), new StringExpression("value"));

        Map<Expression, Expression> map = new HashMap<>();
        map.put(new StringExpression("key"), new MapExpression(nestedMap));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new MapExpression(map));

        expressions.add(new MethodCallExpression("getNestedMap", parameters));

        // maps.getMap({"key"={"key"="value"}})
        assertEquals("value", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testOptionalMap() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("maps"));

        List<Expression> parameters = new ArrayList<>();

        expressions.add(new MethodCallExpression("getOptionalMap", parameters));

        // maps.getOptionalMap()
        assertEquals("null", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void testList() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("lists"));

        List<Expression> parameters = new ArrayList<>();
        parameters.add(new ListExpression(Arrays.asList(new StringExpression("test"), new DoubleExpression(12.67), new StringExpression("expected"))));

        expressions.add(new MethodCallExpression("getList", parameters));

        // lists.getList(["test", 12.67, "expected"])
        assertEquals("expected", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeMethodWithNamespaceAndOptional() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("getOptional", new ArrayList<>()));

        // test.getOptional()
        assertEquals("null", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void invokeMethodWithNamespaceAndOptionalWhileSpecifyingValue() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("test"));
        expressions.add(new MethodCallExpression("getOptional", Collections.singletonList(new StringExpression("test"))));

        // test.getOptional("test")
        assertEquals("test", buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
    }

    @Test
    public void valueExpressionAsRoot() throws Exception {
        assertEquals("test", buildMethodInvoker().invokeMethod(new StringExpression("test")));
    }

    @Test
    public void methodCallExpressionAsRoot() throws Exception {
        assertEquals(
                12,
                buildMethodInvoker()
                        .invokeMethod(new MethodCallExpression("getIt", Collections.emptyList()))
        );
    }

    @Test(expected = MethodInvocationException.class)
    public void nonExistentExpressionAsRoot() throws Exception {
        buildMethodInvoker().invokeMethod(new Expression() {
            @Override
            public String toString() {
                return null;
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        });
    }

    @Test(expected = MethodInvocationException.class)
    public void nonExistentParameter() throws Exception {
        buildMethodInvoker().invokeMethod(
                new MethodCallExpression("getIt", Collections.singletonList(
                        new Expression() {
                            @Override
                            public String toString() {
                                return null;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return false;
                            }

                            @Override
                            public int hashCode() {
                                return 0;
                            }
                        }
                )
                )
        );
    }

    @Test(expected = MethodInvocationException.class)
    public void invalidNumberOfParameters() throws Exception {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new MethodCallExpression(
                "getIt",
                Collections.singletonList(new StringExpression("test")))
        );

        // getIt()
        assertEquals(12, buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions)));
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

    @Test(expected = MethodInvocationException.class)
    public void invalidNamespace() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("invalid"));
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        // invalid.getIt()
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test(expected = MethodInvocationException.class)
    public void invalidClassMethod() throws MethodInvocationException {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("ints"));

        expressions.add(new MethodCallExpression("getInt", Collections.singletonList(new IntegerExpression(12))));
        expressions.add(new MethodCallExpression("getIt", new ArrayList<>()));

        // ints.getInt(getIt())
        buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions));
    }

    @Test(expected = MethodRegistrationException.class)
    public void nonStaticMethodThrows() throws MethodRegistrationException {
        MethodInvoker methodInvoker = new MethodInvoker(new ParameterConverterManager());
        methodInvoker.registerMethods(NonStaticMethod.class);
    }

    @Test(expected = MethodRegistrationException.class)
    public void invalidOperatesOnThrows() throws MethodRegistrationException {
        MethodInvoker methodInvoker = new MethodInvoker(new ParameterConverterManager());
        methodInvoker.registerMethods(NoValidOperatesOn.class);
    }

    @Test(expected = MethodRegistrationException.class)
    public void unmatchingValidOperatesOn() throws MethodRegistrationException {
        MethodInvoker methodInvoker = new MethodInvoker(new ParameterConverterManager());
        methodInvoker.registerMethods(UnmatchingValidOperatesOn.class);
    }

    @Test
    public void namespaceOnClass() throws MethodRegistrationException {
        MethodInvoker methodInvoker = new MethodInvoker(new ParameterConverterManager());
        methodInvoker.registerMethods(NamespaceOnClass.class);
    }

    @Test(expected = MethodAccessDeniedException.class)
    public void invokerNoPermissionTest() throws MethodInvocationException {
        MethodInvoker methodInvoker = buildMethodInvoker();

        Queue<Boolean> queue = new LinkedBlockingQueue<>(Collections.singletonList(false));

        TestInvoker invoker = new TestInvoker(null, queue);

        methodInvoker.invokeMethod(new MethodCallExpression("getIt", Collections.emptyList()), invoker);
    }

    @Test
    public void invokerWithParameter() throws MethodInvocationException {
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(String.class, "test");
        TestInvoker invoker = new TestInvoker(map, null);
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(new MethodCallExpression("getString", new ArrayList<>()));

        // objects.getString()
        assertEquals(
                "test",
                buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions), invoker)
        );
    }

    @Test
    public void invokerWithParameterOnMultipleParameterMethod() throws MethodInvocationException {
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(int.class, 12);
        TestInvoker invoker = new TestInvoker(map, null);
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new NamespaceExpression("objects"));
        expressions.add(
                new MethodCallExpression(
                        "getMultipleString",
                        Arrays.asList(
                                new StringExpression("test"),
                                new StringExpression("test")
                        )
                )
        );

        // objects.getString("test")
        assertEquals(
                "testtest12",
                buildMethodInvoker().invokeMethod(new ChainedMethodCallExpression(expressions), invoker)
        );
    }

    @Test
    public void allMethodsAreRegistered() {
        MethodInvoker methodInvoker = buildMethodInvoker();
        assertEquals(10, methodInvoker.getNamespaces().size());
        assertEquals(3, methodInvoker.getClasses().size());
    }

    private MethodInvoker buildMethodInvoker() {
        MethodInvoker methodInvoker = new MethodInvoker(buildParameterConverterManager());
        methodInvoker.registerMethods(new MethodInvokerTestClass());

        return methodInvoker;
    }

    private ParameterConverterManager buildParameterConverterManager() {
        ParameterConverterManager manager = new ParameterConverterManager();

        manager.registerParameterConverter(String.class, UUID.class, UUID::fromString);
        manager.registerParameterConverter(MethodInvokerTestClass.MethodInvokerTestObject.class, int.class, MethodInvokerTestClass.MethodInvokerTestObject::getInt);
        manager.registerParameterConverter(MethodInvokerTestClass.MethodInvokerTestInterface.class, boolean.class, MethodInvokerTestClass.MethodInvokerTestInterface::getBoolean);

        return manager;
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

        @APIMethod(namespace = "test")
        public static String getOptional(@OptionalParam String that) {
            return String.valueOf(that);
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

        @APIMethod(namespace = "maps")
        public static String getMap(Map<Object, Object> arg) {
            return arg.get("key").toString();
        }

        @SuppressWarnings("unchecked")
        @APIMethod(namespace = "maps")
        public static String getNestedMap(Map<Object, Object> arg) {
            return ((Map<Object, Object>) arg.get("key")).get("key").toString();
        }

        @APIMethod(namespace = "maps")
        public static String getOptionalMap(@OptionalParam Map<Object, Object> arg) {
            return String.valueOf(arg);
        }

        @APIMethod(namespace = "lists")
        public static String getList(List<Object> arg) {
            return arg.get(2).toString();
        }

        @APIMethod(namespace = "lists")
        public static List<String> create() {
            return Collections.singletonList("test");
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObject getObject() {
            return new MethodInvokerTestObject();
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObject getNull() {
            return null;
        }

        @APIMethod(namespace = "objects")
        public static MethodInvokerTestObjectExtension getObjectExtension() {
            return new MethodInvokerTestObjectExtension();
        }

        @APIMethod(namespace = "objects")
        public static String getUUID(UUID uuid) {
            return uuid.toString();
        }

        @APIMethod(namespace = "objects")
        public static String getString(String string) {
            return string;
        }

        @APIMethod(namespace = "objects")
        public static String getMultipleString(String string, String it, int arg) {
            return string + it + arg;
        }

        @APIMethod(namespace = "exceptions")
        public static void throwNormal() throws Exception {
            throw new Exception();
        }

        @APIMethod(namespace = "exceptions")
        public static void throwRethrowable() throws Exception {
            throw new APIException("Test", 12);
        }

        @APIMethod(operatesOn = Object.class)
        public static String getName(Object object) {
            return object.getClass().getSimpleName();
        }

        @APIMethod(operatesOn = List.class)
        public static String getFirstItem(List list) {
            return list.get(0).toString();
        }

        @APIMethod(operatesOn = MethodInvokerTestObject.class)
        public static int getInt(MethodInvokerTestObject methodInvokerTestObject) {
            return methodInvokerTestObject.getInt();
        }

        private static class MethodInvokerTestObject {
            public Integer getInt() {
                return 18;
            }
        }

        private static class MethodInvokerTestObjectExtension extends MethodInvokerTestObject implements MethodInvokerTestInterface {
            public Integer getInt() {
                return 19;
            }

            @Override
            public boolean getBoolean() {
                return false;
            }
        }

        private interface MethodInvokerTestInterface {
            boolean getBoolean();
        }
    }

    private static class NonStaticMethod {
        @APIMethod
        public String getIt() {
            return "";
        }
    }

    private static class NoValidOperatesOn {
        @APIMethod(operatesOn = Object.class)
        public static String getObject() {
            return "";
        }
    }

    private static class UnmatchingValidOperatesOn {
        @APIMethod(operatesOn = String.class)
        public static String getObject(Object self) {
            return self.toString();
        }
    }

    @APINamespace("test")
    private static class NamespaceOnClass {
        @APIMethod
        public static String getIt() {
            return "";
        }
    }

    private static class TestInvoker implements InvokeParameters {
        private Map<Class<?>, Object> objects;
        private Queue<Boolean> hasPermissions;

        public TestInvoker(Map<Class<?>, Object> objects, Queue<Boolean> hasPermissions) {
            this.objects = objects;
            this.hasPermissions = hasPermissions;
        }

        @Override
        public Object get(Class<?> clazz) {
            if (objects == null) {
                return null;
            }
            return objects.get(clazz);
        }

        @Override
        public boolean checkPermission(Permissible object) {
            if (hasPermissions == null) {
                return true;
            }
            return hasPermissions.poll();
        }
    }
}
