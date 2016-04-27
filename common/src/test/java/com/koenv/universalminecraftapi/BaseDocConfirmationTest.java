package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import com.koenv.universalminecraftapi.users.UserManager;
import com.koenv.universalminecraftapi.users.model.User;

import static org.junit.Assert.assertEquals;

public abstract class BaseDocConfirmationTest {
    public void confirmUserExample(UsersConfiguration configuration) {
        UserManager manager = new UserManager();
        manager.loadConfiguration(configuration);

        User user = manager.getUser("default").get();

        assertEquals(11, user.getPermission("players.get"));
        assertEquals(10, user.getPermission("players.chat"));
        assertEquals(1, user.getPermission("uma.platform"));
        assertEquals(1, user.getPermission("streams.subscribe"));
        assertEquals(-1, user.getPermission("streams.unsubscribe"));
        assertEquals(1, user.getPermission("streams.chat"));
        assertEquals(-1, user.getPermission("streams.console"));
        assertEquals(0, user.getPermission("server.broadcast"));

        user = manager.getUser("admin").get();

        assertEquals(1, user.getPermission("players.get"));
        assertEquals(1, user.getPermission("server.broadcast"));
        assertEquals(2, user.getPermission("streams.subscribe"));
        assertEquals(0, user.getPermission("streams.unsubscribe"));
        assertEquals(2, user.getPermission("streams.chat"));
        assertEquals(0, user.getPermission("streams.console"));
    }

    public void confirmGroupExample(UsersConfiguration configuration) {
        UserManager manager = new UserManager();
        manager.loadConfiguration(configuration);

        User user = manager.getUser("default").get();

        assertEquals(1, user.getPermission("players"));
        assertEquals(0, user.getPermission("players.get"));
        assertEquals(1, user.getPermission("uma"));
    }
}
