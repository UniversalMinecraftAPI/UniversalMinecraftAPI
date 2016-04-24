package com.koenv.universalminecraftapi.http.rest;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RestHandlerManager {
    @Test
    public void resourceRegistration() throws Exception {
        RestHandler handler = new RestHandler();
        handler.registerClass(GoodResources.class);

        assertEquals(1, handler.getResources().size());

        RestResourceMethod method = handler.getResources().get(0);

        assertEquals("players/:name", method.getPath());
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badResourceRegistration() throws Exception {
        RestHandler handler = new RestHandler();
        handler.registerClass(BadResource.class);
    }

    @Test
    public void operationRegistration() throws Exception {
        RestHandler handler = new RestHandler();
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
        new RestHandler().registerClass(BadRestOperation1.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation2() throws Exception {
        new RestHandler().registerClass(BadRestOperation2.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation3() throws Exception {
        new RestHandler().registerClass(BadRestOperation3.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation4() throws Exception {
        new RestHandler().registerClass(BadRestOperation4.class);
    }

    @Test(expected = RestMethodRegistrationException.class)
    public void badRestOperation5() throws Exception {
        new RestHandler().registerClass(BadRestOperation5.class);
    }

    @Test
    public void onlyResource() throws Exception {
        Object result = buildRestHandler().handle("players/test", null);

        assertEquals(new TestPlayer("test"), result);
    }

    @Test
    public void resourceAndOperation() throws Exception {
        Object result = buildRestHandler().handle("players/newname/name", null);

        assertEquals("newname", result);
    }

    @Test
    public void resourceAndMultipleOperations() throws Exception {
        Object result = buildRestHandler().handle("players/newname/name/hash", null);

        assertEquals(1845974059, result);
    }

    private RestHandler buildRestHandler() throws Exception {
        RestHandler handler = new RestHandler();
        handler.registerClass(TestResourcesAndOperations.class);

        return handler;
    }

    public static class GoodResources {
        @RestResource("players/:name")
        public static TestPlayer getPlayer(@RestPath("name") String name) {
            return new TestPlayer(name);
        }
    }

    public static class BadResource {
        @RestResource("players/:name")
        public static TestPlayer getPlayer(String it) {
            return new TestPlayer(it);
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
    }

    @Override
    public String toString() {
        return "RestHandlerManager{}";
    }
}
