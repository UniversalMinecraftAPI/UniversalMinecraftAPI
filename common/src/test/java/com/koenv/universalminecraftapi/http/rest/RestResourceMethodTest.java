package com.koenv.universalminecraftapi.http.rest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RestResourceMethodTest {
    @Test
    public void exactPathMatch() {
        RestResourceMethod route = new RestResourceMethod("test/test", null);

        assertTrue(route.matches("test/test"));
    }

    @Test
    public void singleParamMatch() {
        RestResourceMethod route = new RestResourceMethod(":test", null);

        assertTrue(route.matches("test"));
    }

    @Test
    public void multipleParamMatch() {
        RestResourceMethod route = new RestResourceMethod(":test/:test2", null);

        assertTrue(route.matches("players/test"));
    }

    @Test
    public void moreRouteMatch() {
        RestResourceMethod route = new RestResourceMethod(":namespace/:method", null);

        assertTrue(route.matches("players/test/uuid"));
    }

    @Test
    public void lessRouteNoMatch() {
        RestResourceMethod route = new RestResourceMethod(":namespace/:method", null);

        assertFalse(route.matches("players"));
    }
}
