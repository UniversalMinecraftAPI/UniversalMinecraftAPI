package com.koenv.universalminecraftapi.sponge;

import com.koenv.universalminecraftapi.BaseDocConfirmationTest;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.junit.Test;

import java.nio.file.Paths;

public class SpongeDocConfirmationTest extends BaseDocConfirmationTest {
    @Test
    public void usersExample() throws Exception {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(Paths.get(getClass().getResource("/users1.conf").toURI()))
                .build();
        CommentedConfigurationNode root = loader.load();
        UsersConfiguration configuration = SpongeConfigurationLoader.loadUsersConfiguration(root);

        confirmUserExample(configuration);
    }

    @Test
    public void groupExample() throws Exception {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(Paths.get(getClass().getResource("/group1.conf").toURI()))
                .build();
        CommentedConfigurationNode root = loader.load();
        UsersConfiguration configuration = SpongeConfigurationLoader.loadUsersConfiguration(root);

        confirmGroupExample(configuration);
    }
}
