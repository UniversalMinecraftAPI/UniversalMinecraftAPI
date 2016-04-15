package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.config.JSONAPIRootConfiguration;
import com.koenv.jsonapi.config.WebServerSection;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.config.user.*;
import com.koenv.jsonapi.users.model.PermissionType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class SpigotConfigurationLoader {
    public static JSONAPIRootConfiguration load(ConfigurationSection node) {
        return JSONAPIRootConfiguration.builder()
                .webServer(loadWebServer(node.getConfigurationSection("web_server")))
                .build();
    }

    private static WebServerSection loadWebServer(ConfigurationSection node) {
        return WebServerSection.builder()
                .ipAddress(node.getString("ip_address"))
                .port(node.getInt("port", -1))
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
                .permissions(loadPermissions(node.getConfigurationSection("permissions")))
                .build();
    }

    private static List<UserSection> loadUsers(ConfigurationSection node) {
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
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadGroup).collect(Collectors.toList());
    }

    private static GroupSection loadGroup(ConfigurationSection node) {
        return GroupSection.builder()
                .name(node.getName())
                .permissions(node.getStringList("permissions"))
                .build();
    }

    private static List<PermissionSection> loadPermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadPermission).collect(Collectors.toList());
    }

    private static PermissionSection loadPermission(ConfigurationSection node) {
        return PermissionSection.builder()
                .name(node.getName())
                .namespaces(loadNamespacePermissions(node.getConfigurationSection("namespaces")))
                .classes(loadClassPermissions(node.getConfigurationSection("classes")))
                .streams(loadStreamPermission(node.getConfigurationSection("streams")))
                .build();
    }

    private static List<NamespacePermissionSection> loadNamespacePermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadNamespacePermission).collect(Collectors.toList());
    }

    private static NamespacePermissionSection loadNamespacePermission(ConfigurationSection node) {
        return NamespacePermissionSection.builder()
                .name(node.getName())
                .type(PermissionType.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .methods(node.getStringList("methods"))
                .build();
    }

    private static List<ClassPermissionSection> loadClassPermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(SpigotConfigurationLoader::loadClassPermission).collect(Collectors.toList());
    }

    private static ClassPermissionSection loadClassPermission(ConfigurationSection node) {
        return ClassPermissionSection.builder()
                .name(node.getName())
                .type(PermissionType.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .methods(node.getStringList("methods"))
                .build();
    }

    private static StreamPermissionSection loadStreamPermission(ConfigurationSection node) {
        return StreamPermissionSection.builder()
                .type(PermissionType.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .streams(node.getStringList("streams"))
                .build();
    }
}
