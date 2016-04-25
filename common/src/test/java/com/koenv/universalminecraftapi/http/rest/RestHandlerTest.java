package com.koenv.universalminecraftapi.http.rest;

import com.koenv.universalminecraftapi.reflection.ParameterConverterManager;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RestHandlerTest {
    @Test
    public void resourceRegistration() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(GoodResources.class);

        assertEquals(1, handler.getResources().size());

        RestResourceMethod method = handler.getResources().get(0);

        assertEquals("players/:name", method.getPath());
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badResource1() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(BadResource1.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestResource2() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(BadResource2.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestResource3() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(BadResource3.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestResource4() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(BadResource4.class);
    }

    @Test
    public void operationRegistration() throws Exception {
        RestHandler handler = new RestHandler(null);
        handler.registerClass(GoodRestOperations.class);

        assertEquals(1, handler.getOperations().size());

        int count = 0;

        for (RestOperationMethod method : handler.getOperations().get(TestPlayer.class).values()) {
            switch (method.getPath()) {
                case "name":
                    assertEquals("name", method.getPath());
                    assertEquals(RestMethod.GET, method.getRestMethod());
                    assertEquals(TestPlayer.class, method.getOperatesOn());
                    count++;
                    break;
                case "uuid":
                    assertEquals("uuid", method.getPath());
                    assertEquals(RestMethod.GET, method.getRestMethod());
                    assertEquals(TestPlayer.class, method.getOperatesOn());
                    count++;
                    break;
                case "nickname":
                    assertEquals("nickname", method.getPath());
                    assertEquals(RestMethod.POST, method.getRestMethod());
                    assertEquals(TestPlayer.class, method.getOperatesOn());
                    count++;
                    break;
                default:
                    fail("Invalid operation method found");
            }
        }

        assertEquals("Not all expected registered methods were found", 3, count);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation1() throws Exception {
        new RestHandler(null).registerClass(BadRestOperation1.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation2() throws Exception {
        new RestHandler(null).registerClass(BadRestOperation2.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation3() throws Exception {
        new RestHandler(null).registerClass(BadRestOperation3.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation4() throws Exception {
        new RestHandler(null).registerClass(BadRestOperation4.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation5() throws Exception {
        new RestHandler(null).registerClass(BadRestOperation5.class);
    }

    @Test
    public void onlyResource() throws Exception {
        Object result = buildRestHandler().handle("players/test", new TestRestParameters());

        assertEquals(new TestPlayer("test"), result);
    }

    @Test
    public void resourceAndOperation() throws Exception {
        Object result = buildRestHandler().handle("players/newname/name", new TestRestParameters());

        assertEquals("newname", result);
    }

    @Test
    public void resourceAndMultipleOperations() throws Exception {
        Object result = buildRestHandler().handle("players/newname/name/hash", new TestRestParameters());

        assertEquals(1845974059, result);
    }

    @Test
    public void resourceWithParameters() throws Exception {
        Object result = buildRestHandler().handle("objects/test/name", new TestRestParameters() {
            @Override
            public Object get(Class<?> clazz) {
                if (clazz == String.class) {
                    return "myname";
                }
                return null;
            }
        });

        assertEquals("testmyname", result);
    }

    @Test
    public void operationWithParameters() throws Exception {
        Object result = buildRestHandler().handle("players/thetest/it", new TestRestParameters() {
            @Override
            public Object get(Class<?> clazz) {
                if (clazz == String.class) {
                    return "isgreat";
                }
                return null;
            }
        });

        assertEquals("thetestisgreat", result);
    }

    @Test(expected = RestForbiddenException.class)
    public void resourceWithNoPermission() throws Exception {
        buildRestHandler().handle("players/newname", new TestRestParameters() {
            @Override
            public boolean hasPermission(RestResourceMethod method) {
                return !Objects.equals(method.getPath(), "players/:name");
            }
        });
    }

    @Test(expected = RestForbiddenException.class)
    public void operationWithNoPermission() throws Exception {
        buildRestHandler().handle("players/newname/name", new TestRestParameters() {
            @Override
            public boolean hasPermission(RestOperationMethod method) {
                return !Objects.equals(method.getPath(), "name");
            }
        });
    }

    @Test(expected = RestNotFoundException.class)
    public void nonExistentResource() throws Exception {
        buildRestHandler().handle("players", new TestRestParameters());
    }

    @Test(expected = RestNotFoundException.class)
    public void missingParameter() throws Exception {
        buildRestHandler().handle("players/", new TestRestParameters());
    }

    @Test
    public void singleQueryParameter() throws Exception {
        assertEquals("querytest", buildRestHandler().handle("queries/single", new TestRestParameters() {
            @Override
            public RestQueryParamsMap getQueryParams() {
                return new TestRestQueryParamMap() {
                    @Override
                    public RestQueryParamsMap get(String key) {
                        if (key.equals("name")) {
                            return new TestRestQueryParamMap() {
                                @Override
                                public boolean hasValue() {
                                    return true;
                                }

                                @Override
                                public String value() {
                                    return "querytest";
                                }
                            };
                        }
                        return null;
                    }
                };
            }
        }));
    }

    @Test
    public void listQueryParameter() throws Exception {
        assertEquals("[querytest, anothertest]", buildRestHandler().handle("queries/list", new TestRestParameters() {
            @Override
            public RestQueryParamsMap getQueryParams() {
                return new TestRestQueryParamMap() {
                    @Override
                    public RestQueryParamsMap get(String key) {
                        if (key.equals("names")) {
                            return new TestRestQueryParamMap() {
                                @Override
                                public boolean hasValues() {
                                    return true;
                                }

                                @Override
                                public List<String> values() {
                                    return Arrays.asList("querytest", "anothertest");
                                }
                            };
                        }
                        return null;
                    }
                };
            }
        }));
    }

    @Test
    // TODO: A better way to create this method, because right now it is ugly and incomprehensible
    public void mapQueryParameter() throws Exception {
        assertEquals("{key1=value1, key2={key=value}, key3=[test1, test2]}", buildRestHandler().handle("queries/map", new TestRestParameters() {
            @Override
            public RestQueryParamsMap getQueryParams() {
                return new TestRestQueryParamMap() {
                    @Override
                    public RestQueryParamsMap get(String key) {
                        if (key.equals("parameters")) {
                            return new TestRestQueryParamMap() {
                                @Override
                                public boolean hasChildren() {
                                    return true;
                                }

                                @Override
                                public List<String> getKeys() {
                                    return Arrays.asList("key1", "key2", "key3");
                                }

                                @Override
                                public RestQueryParamsMap get(String key) {
                                    switch (key) {
                                        case "key1":
                                            return new TestRestQueryParamMap() {
                                                @Override
                                                public boolean hasValue() {
                                                    return true;
                                                }

                                                @Override
                                                public String value() {
                                                    return "value1";
                                                }
                                            };
                                        case "key2":
                                            return new TestRestQueryParamMap() {
                                                @Override
                                                public boolean hasChildren() {
                                                    return true;
                                                }

                                                @Override
                                                public List<String> getKeys() {
                                                    return Collections.singletonList("key");
                                                }

                                                @Override
                                                public RestQueryParamsMap get(String key) {
                                                    if (Objects.equals(key, "key")) {
                                                        return new TestRestQueryParamMap() {
                                                            @Override
                                                            public boolean hasValue() {
                                                                return true;
                                                            }

                                                            @Override
                                                            public String value() {
                                                                return "value";
                                                            }
                                                        };
                                                    }
                                                    return null;
                                                }
                                            };
                                        case "key3":
                                            return new TestRestQueryParamMap() {
                                                @Override
                                                public boolean hasValues() {
                                                    return true;
                                                }

                                                @Override
                                                public List<String> values() {
                                                    return Arrays.asList("test1", "test2");
                                                }
                                            };
                                        default:
                                            return null;
                                    }
                                }
                            };
                        }
                        return null;
                    }

                    @Override
                    public boolean hasChildren() {
                        return true;
                    }

                    @Override
                    public List<String> getKeys() {
                        return Collections.singletonList("parameters");
                    }
                };
            }
        }));
    }

    @Test
    public void testStringBody() throws Exception {
        assertEquals("thisisatestvalue", buildRestHandler().handle("players/test/body", new TestRestParameters() {
            @Override
            public @Nullable Object getBody() {
                return "thisisatestvalue";
            }

            @Override
            public @NotNull RestMethod getMethod() {
                return RestMethod.POST;
            }
        }));
    }

    @Test
    public void testJSONArrayBody() throws Exception {
        assertEquals("[test, testvalue2]", buildRestHandler().handle("players/test/bodyList", new TestRestParameters() {
            @Override
            public @Nullable Object getBody() {
                return new JSONArray(Arrays.asList("test", "testvalue2"));
            }

            @Override
            public @NotNull RestMethod getMethod() {
                return RestMethod.POST;
            }
        }));
    }

    @Test
    public void testJSONObjectBody() throws Exception {
        assertEquals("{key=value}", buildRestHandler().handle("players/test/bodyMap", new TestRestParameters() {
            @Override
            public @Nullable Object getBody() {
                JSONObject result = new JSONObject();
                result.put("key", "value");
                return result;
            }

            @Override
            public @NotNull RestMethod getMethod() {
                return RestMethod.POST;
            }
        }));
    }

    @Test(expected = RestMethodInvocationException.class)
    public void testIntermediatePostThrows() throws Exception {
        buildRestHandler().handle("players/test/bodyMap/hash", new TestRestParameters() {
            @Override
            public @NotNull RestMethod getMethod() {
                return RestMethod.POST;
            }
        });
    }

    private RestHandler buildRestHandler() throws Exception {
        RestHandler handler = new RestHandler(new ParameterConverterManager());
        handler.registerClass(TestResourcesAndOperations.class);

        return handler;
    }

    public static class GoodResources {
        @RestResource("players/:name")
        public static TestPlayer getPlayer(@RestPath("name") String name) {
            return new TestPlayer(name);
        }
    }

    public static class BadResource1 {
        @RestResource("players/:name")
        public static TestPlayer getPlayer(@RestPath("it") String it) {
            return new TestPlayer(it);
        }
    }

    public static class BadResource2 {
        @RestResource("players/:it/:name")
        public static TestPlayer getPlayer(@RestPath("it") String it) {
            return new TestPlayer(it);
        }
    }

    public static class BadResource3 {
        @RestResource("players/:it")
        public static TestPlayer getPlayer(@RestPath("it") String it, @RestPath("name") String name) {
            return new TestPlayer(it + name);
        }
    }

    public static class BadResource4 {
        @RestResource("players")
        public static TestPlayer getPlayer(@RestBody String body) {
            return new TestPlayer(body);
        }
    }

    public static class GoodRestOperations {
        @RestOperation(TestPlayer.class)
        public static String getName(TestPlayer self) {
            return "test";
        }

        @RestOperation(value = TestPlayer.class, path = "uuid")
        public static String getUUID(TestPlayer self) {
            return UUID.nameUUIDFromBytes(new byte[]{0x01}).toString();
        }

        @RestOperation(value = TestPlayer.class)
        public static boolean setNickname(TestPlayer self, @RestBody String nickname) {
            return true;
        }
    }

    public static class BadRestOperation1 {
        @RestOperation(TestPlayer.class)
        public static String getName() {
            return "test";
        }
    }

    public static class BadRestOperation2 {
        @RestOperation(TestPlayer.class)
        public static String getName(String self) {
            return "test";
        }
    }

    public static class BadRestOperation3 {
        @RestOperation(value = TestPlayer.class, method = RestMethod.GET)
        public static String getName(TestPlayer self, @RestBody String body) {
            return "test";
        }
    }

    public static class BadRestOperation4 {
        @RestOperation(TestPlayer.class)
        public static String getName(TestPlayer self, @RestBody String body) {
            return "test";
        }
    }

    public static class BadRestOperation5 {
        @RestOperation(TestPlayer.class)
        public static String getName(TestPlayer self, @RestBody String body, @RestBody String body2) {
            return "test";
        }
    }

    public static class TestResourcesAndOperations {
        @RestResource("players/:name")
        public static TestPlayer getPlayer(@RestPath("name") String name) {
            return new TestPlayer(name);
        }

        @RestOperation(TestPlayer.class)
        public static String getName(TestPlayer self) {
            return self.getName();
        }

        @RestOperation(String.class)
        public static int getHash(String self) {
            return self.hashCode();
        }

        @RestOperation(TestPlayer.class)
        public static String setBody(TestPlayer self, @RestBody String body) {
            return body;
        }

        @RestOperation(TestPlayer.class)
        public static String setBodyList(TestPlayer self, @RestBody List<String> body) {
            return body.toString();
        }

        @RestOperation(TestPlayer.class)
        public static String setBodyMap(TestPlayer self, @RestBody Map<String, Object> body) {
            return body.toString();
        }

        @RestResource("objects/:test")
        public static TestPlayer getTest(@RestPath("test") String test, String extra) {
            return new TestPlayer(test + extra);
        }

        @RestOperation(TestPlayer.class)
        public static String getIt(TestPlayer self, String extra) {
            return self.getName() + extra;
        }

        @RestResource("queries/single")
        public static String getFromQuery(@RestQuery("name") String name) {
            return name;
        }

        @RestResource("queries/list")
        public static String getFromQueryList(@RestQuery("names") List<String> names) {
            return names.toString();
        }

        @RestResource("queries/map")
        public static String getFromQueryMap(@RestQuery("parameters") Map<String, Object> parameters) {
            return parameters.toString();
        }
    }

    public static class TestPlayer {
        private String name;

        public TestPlayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestPlayer that = (TestPlayer) o;

            return name != null ? name.equals(that.name) : that.name == null;

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "TestPlayer{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private static class TestRestParameters implements RestParameters {
        @Override
        public boolean hasPermission(RestResourceMethod method) {
            return true;
        }

        @Override
        public boolean hasPermission(RestOperationMethod method) {
            return true;
        }

        @Override
        public Object get(Class<?> clazz) {
            return null;
        }

        @Override
        public RestQueryParamsMap getQueryParams() {
            return null;
        }

        @Override
        public @Nullable Object getBody() {
            return null;
        }

        @Override
        public @NotNull RestMethod getMethod() {
            return RestMethod.GET;
        }
    }

    private static class TestRestQueryParamMap implements RestQueryParamsMap {

        @Override
        public RestQueryParamsMap get(String key) {
            return null;
        }

        @Override
        public boolean hasValue() {
            return false;
        }

        @Override
        public String value() {
            return null;
        }

        @Override
        public boolean hasValues() {
            return false;
        }

        @Override
        public List<String> values() {
            return null;
        }

        @Override
        public boolean hasChildren() {
            return false;
        }

        @Override
        public List<String> getKeys() {
            return null;
        }
    }
}
