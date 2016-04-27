package com.koenv.universalminecraftapi.sponge;

import com.koenv.universalminecraftapi.config.user.GroupSection;
import com.koenv.universalminecraftapi.config.user.PermissionSection;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SpongeConfigurationLoaderTest {
    @Test
    public void testDefaultConfigLoads() throws Exception {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(Paths.get(getClass().getResource("defaultConfig.conf").toURI()))
                .build();
        CommentedConfigurationNode root = loader.load();
        SpongeConfigurationLoader.loadRoot(root);
    }

    @Test
    public void testDefaultUsersConfigLoads() throws Exception {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(Paths.get(getClass().getResource("defaultUsers.conf").toURI()))
                .build();
        CommentedConfigurationNode root = loader.load();
        UsersConfiguration configuration = SpongeConfigurationLoader.loadUsersConfiguration(root);

        assertEquals(2, configuration.getUsers().size());

        assertEquals(2, configuration.getGroups().size());

        GroupSection firstGroup = configuration.getGroups().get(0);
        assertEquals("default", firstGroup.getName());
        assertEquals(0, firstGroup.getDefaultPermission());
        assertEquals(0, firstGroup.getInheritsFrom().size());
        assertEquals(3, firstGroup.getPermissions().size());

        assertThat(firstGroup.getPermissions(), hasItem(PermissionSection.builder().name("players").value(10).build()));
        assertThat(firstGroup.getPermissions(), hasItem(PermissionSection.builder().name("players.get").value(1).build()));
        assertThat(firstGroup.getPermissions(), hasItem(PermissionSection.builder().name("uma").value(1).build()));
    }
}
