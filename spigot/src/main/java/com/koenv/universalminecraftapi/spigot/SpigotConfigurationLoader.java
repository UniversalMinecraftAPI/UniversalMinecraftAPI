package com.koenv.universalminecraftapi.spigot;

import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.config.WebServerSection;
import com.koenv.universalminecraftapi.config.WebServerSecureSection;
import com.koenv.universalminecraftapi.config.WebServerThreadPoolSection;
import com.koenv.universalminecraftapi.config.user.GroupSection;
import com.koenv.universalminecraftapi.config.user.PermissionSection;
import com.koenv.universalminecraftapi.config.user.UserSection;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpigotConfigurationLoader {
    public static UniversalMinecraftAPIRootConfiguration load(ConfigurationSection node) throws InvalidConfigurationException {
        return UniversalMinecraftAPIRootConfiguration.builder()
                .webServer(loadWebServer(node.getConfigurationSection("web_server")))
                .build();
    }

    private static WebServerSection loadWebServer(ConfigurationSection node) throws InvalidConfigurationException {
        if (node == null) {
            throw new InvalidConfigurationException("Missing web_server section");
        }
        return WebServerSection.builder()
                .ipAddress(node.getString("ip_address"))
                .port(node.getInt("port", -1))
                .ipWhitelist(node.getStringList("ip_whitelist"))
                .secure(loadWebServerSecure(node.getConfigurationSection("secure")))
                .threadPool(loadWebServerThreadPool(node.getConfigurationSection("thread_pool")))
                .build();
    }

    private static WebServerSecureSection loadWebServerSecure(ConfigurationSection node) {
        return WebServerSecureSection.builder()
                .enabled(node.getBoolean("enabled"))
                .keyStoreFile(node.getString("keystore.file"))
                .keystorePassword(node.getString("keystore.password"))
                .trustStoreFile(node.getString("truststore.file"))
                .trustStorePassword(node.getString("truststore.password"))
                .build();
    }

    private static WebServerThreadPoolSection loadWebServerThreadPool(ConfigurationSection node) {
        return WebServerThreadPoolSection.builder()
                .maxThreads(node.getInt("max_threads", -1))
                .minThreads(node.getInt("min_threads", -1))
                .idleTimeoutMillis(node.getInt("idle_timeout", -1))
                .build();
    }

    public static UsersConfiguration loadUsersConfiguration(ConfigurationSection node) {
        return UsersConfiguration.builder()
                .users(loadUsers(node.getConfigurationSection("users")))
                .groups(loadGroups(node.getConfigurationSection("groups")))
                .build();
    }

    private static List<UserSection> loadUsers(ConfigurationSection node) {
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadUser).collect(Collectors.toList());
    }

    private static UserSection loadUser(ConfigurationSection node) {
        return UserSection.builder()
                .username(node.getName())
                .password(node.getString("password"))
                .passwordType(node.getString("password_type", "plain"))
                .groups(node.getStringList("groups"))
                .build();
    }

    private static List<GroupSection> loadGroups(ConfigurationSection node) {
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadGroup).collect(Collectors.toList());
    }

    private static GroupSection loadGroup(ConfigurationSection node) {
        return GroupSection.builder()
                .name(node.getName())
                .defaultPermission(node.getInt("default_permission", 0))
                .inheritsFrom(node.getStringList("inherits_from"))
                .permissions(loadPermissions(node.getConfigurationSection("permissions")))
                .build();
    }

    private static List<PermissionSection> loadPermissions(ConfigurationSection node) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<PermissionSection> sections = new ArrayList<>();
        node.getKeys(true).forEach(s -> {
                    if (node.isConfigurationSection(s)) {
                        return;
                    }
                    if (s.endsWith("default")) {
                        sections.add(
                                PermissionSection.builder()
                                        .name(s.substring(0, s.length() - 8))
                                        .value(node.getInt(s, 0))
                                        .build()
                        );
                        return;
                    }
                    sections.add(
                            PermissionSection.builder()
                                    .name(s)
                                    .value(node.getInt(s, 0))
                                    .build()
                    );
                }
        );

        return sections;
    }
}
