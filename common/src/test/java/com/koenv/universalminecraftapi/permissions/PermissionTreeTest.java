package com.koenv.universalminecraftapi.permissions;

import com.koenv.universalminecraftapi.util.Pair;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PermissionTreeTest {
    @Test
    public void simpleTest() throws Exception {
        PermissionTree permissionTree = PermissionTree.of(
                Arrays.asList(
                        Pair.of("players", 10),
                        Pair.of("players.get", -100),
                        Pair.of("uma", 20)
                ),
                1
        );

        assertEquals(11, permissionTree.get("players"));
        assertEquals(-89, permissionTree.get("players.get"));
        assertEquals(21, permissionTree.get("uma"));
        assertEquals(1, permissionTree.get("test"));
    }
}