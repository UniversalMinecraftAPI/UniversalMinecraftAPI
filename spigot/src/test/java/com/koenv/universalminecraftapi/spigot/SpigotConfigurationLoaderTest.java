package com.koenv.universalminecraftapi.spigot;

import com.koenv.universalminecraftapi.config.user.GroupSection;
import com.koenv.universalminecraftapi.config.user.PermissionSection;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SpigotConfigurationLoaderTest {
    @Test
    public void testDefaultConfigLoads() throws Exception {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.yml"))));
        SpigotConfigurationLoader.load(configuration);
    }

    @Test
    public void testDefaultUsersConfigLoads() throws Exception {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.yml"))));
        UsersConfiguration configuration = SpigotConfigurationLoader.loadUsersConfiguration(config);

        assertEquals(2, configuration.getUsers().size());

        assertEquals(2, configuration.getGroups().size());

        GroupSection firstGroup = configuration.getGroups().get(0);
        assertEquals("default", firstGroup.getName());
        assertEquals(0, firstGroup.getDefaultPermission());
        assertEquals(0, firstGroup.getInheritsFrom().size());
        assertEquals(3, firstGroup.getPermissions().size());

        assertThat(firstGroup.getPermissions(), hasItem(PermissionSection.builder().name("players").value(10).build()));
        assertThat(firstGroup.getPermissions(), hasItem(PermissionSection.builder().name("players.get").value(1).build()));
    }
}
