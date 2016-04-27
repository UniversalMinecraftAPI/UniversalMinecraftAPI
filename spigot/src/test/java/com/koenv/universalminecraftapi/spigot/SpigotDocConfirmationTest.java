package com.koenv.universalminecraftapi.spigot;

import com.koenv.universalminecraftapi.BaseDocConfirmationTest;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SpigotDocConfirmationTest extends BaseDocConfirmationTest {
    @Test
    public void usersExample() {
        YamlConfiguration configFromFile = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users1.yml"))));

        UsersConfiguration configuration = SpigotConfigurationLoader.loadUsersConfiguration(configFromFile);

        confirmUserExample(configuration);
    }

    @Test
    public void groupExample() {
        YamlConfiguration configFromFile = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/group1.yml"))));

        UsersConfiguration configuration = SpigotConfigurationLoader.loadUsersConfiguration(configFromFile);

        confirmGroupExample(configuration);
    }
}
