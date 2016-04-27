package com.koenv.universalminecraftapi.users;

import com.koenv.universalminecraftapi.config.user.GroupSection;
import com.koenv.universalminecraftapi.config.user.PermissionSection;
import com.koenv.universalminecraftapi.config.user.UserSection;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import com.koenv.universalminecraftapi.users.encoders.PlainTextEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserManagerTest {
    @Test
    public void simpleConfig() {
        UsersConfiguration configuration = UsersConfiguration.builder()
                .users(
                        UserSection.builder()
                                .username("default")
                                .passwordType("plain")
                                .password("default")
                                .groups("default")
                                .build()
                )
                .groups(
                        GroupSection.builder()
                                .name("default")
                                .defaultPermission(1)
                                .permissions(
                                        PermissionSection.builder()
                                                .name("players")
                                                .value(1)
                                                .build(),
                                        PermissionSection.builder()
                                                .name("players.get")
                                                .value(2)
                                                .build(),
                                        PermissionSection.builder()
                                                .name("uma")
                                                .value(1)
                                                .build()
                                )
                                .build()
                )
                .build();

        UserManager userManager = buildUserManager();

        userManager.loadConfiguration(configuration);

        assertEquals(2, userManager.getUser("default").get().getPermission("players"));
        assertEquals(4, userManager.getUser("default").get().getPermission("players.get"));
        assertTrue(userManager.getUser("default").get().hasPermission("players.get"));
        assertTrue(userManager.getUser("default").get().hasPermission("uma"));
        assertTrue(userManager.getUser("default").get().hasPermission("uma.get"));
        assertTrue(userManager.getUser("default").get().hasPermission("uma.get.very.nested.path"));
    }

    @Test
    public void inheritedGroupsConfig() {
        UsersConfiguration configuration = UsersConfiguration.builder()
                .users(
                        UserSection.builder()
                                .username("default")
                                .passwordType("plain")
                                .password("default")
                                .groups("secondgroup")
                                .build()
                )
                .groups(
                        GroupSection.builder()
                                .name("firstgroup")
                                .defaultPermission(1)
                                .permissions(
                                        PermissionSection.builder()
                                                .name("players")
                                                .value(1)
                                                .build()
                                )
                                .build(),
                        GroupSection.builder()
                                .name("secondgroup")
                                .defaultPermission(-1)
                                .inheritsFrom("firstgroup", "thirdgroup")
                                .permissions(
                                        PermissionSection.builder()
                                                .name("uma")
                                                .value(2)
                                                .build()
                                )
                                .build(),
                        GroupSection.builder()
                                .name("thirdgroup")
                                .defaultPermission(0)
                                .permissions(
                                        PermissionSection.builder()
                                                .name("players.get")
                                                .value(-1)
                                                .build()
                                )
                                .build()
                )
                .build();

        UserManager userManager = buildUserManager();

        userManager.loadConfiguration(configuration);

        assertTrue(userManager.getUser("default").get().hasPermission("players.home"));
        assertFalse(userManager.getUser("default").get().hasPermission("players.get"));
        assertTrue(userManager.getUser("default").get().hasPermission("uma"));
    }

    @Test
    public void cyclicInheritedGroupsConfig() {
        UsersConfiguration configuration = UsersConfiguration.builder()
                .users(
                        UserSection.builder()
                                .username("default")
                                .passwordType("plain")
                                .password("default")
                                .groups("secondgroup")
                                .build()
                )
                .groups(
                        GroupSection.builder()
                                .name("firstgroup")
                                .inheritsFrom("secondgroup")
                                .permissions(
                                        PermissionSection.builder()
                                                .name("players")
                                                .value(1)
                                                .build()
                                )
                                .build(),
                        GroupSection.builder()
                                .name("secondgroup")
                                .inheritsFrom("firstgroup")
                                .permissions(
                                        PermissionSection.builder()
                                                .name("uma")
                                                .value(2)
                                                .build(),
                                        PermissionSection.builder()
                                                .name("players")
                                                .value(-2)
                                                .build()
                                )
                                .build()
                )
                .build();

        UserManager userManager = buildUserManager();

        userManager.loadConfiguration(configuration);

        assertFalse(userManager.getUser("default").get().hasPermission("players"));
        assertTrue(userManager.getUser("default").get().hasPermission("uma"));
    }

    @Test
    public void simpleEncoderTest() {
        UsersConfiguration configuration = UsersConfiguration.builder()
                .users(
                        UserSection.builder()
                                .username("default")
                                .password("test")
                                .passwordType("plain")
                                .build()
                )
                .groups()
                .build();

        UserManager userManager = buildUserManager();
        userManager.loadConfiguration(configuration);
        userManager.registerEncoder(new PlainTextEncoder());

        assertTrue(userManager.checkCredentials("default", "test"));
        assertFalse(userManager.checkCredentials("default", "wrongpassword"));
    }

    private UserManager buildUserManager() {
        return new UserManager();
    }
}
