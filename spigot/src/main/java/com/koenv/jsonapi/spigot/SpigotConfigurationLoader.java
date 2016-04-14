package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSection;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.config.user.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class SpigotConfigurationLoader {
    public JSONAPIConfiguration load(FileConfiguration config, FileConfiguration userConfig) {
        return JSONAPIConfiguration.builder()
                .webServer(loadWebServer(config.getConfigurationSection("web_server")))
                .usersConfiguration(loadUsersConfiguration(userConfig))
                .build();
    }

    public WebServerSection loadWebServer(ConfigurationSection section) {
        return WebServerSection.builder()
                .ipAddress(section.getString("ip_address"))
                .port(section.getInt("port", -1))
                .secure(loadWebServerSecure(section.getConfigurationSection("secure")))
                .threadPool(loadWebServerThreadPool(section.getConfigurationSection("thread_pool")))
                .build();
    }

    private WebServerSecureSection loadWebServerSecure(ConfigurationSection node) {
        return WebServerSecureSection.builder()
                .enabled(node.getBoolean("enabled"))
                .keyStoreFile(node.getString("keystore.file"))
                .keystorePassword(node.getString("keystore.password"))
                .trustStoreFile(node.getString("truststore.file"))
                .trustStorePassword(node.getString("truststore.password"))
                .build();
    }

    private WebServerThreadPoolSection loadWebServerThreadPool(ConfigurationSection node) {
        return WebServerThreadPoolSection.builder()
                .maxThreads(node.getInt("max_threads", -1))
                .minThreads(node.getInt("min_threads", -1))
                .idleTimeoutMillis(node.getInt("idle_timeout", -1))
                .build();
    }

    private UsersConfiguration loadUsersConfiguration(ConfigurationSection node) {
        return UsersConfiguration.builder()
                .users(loadUsers(node.getConfigurationSection("users")))
                .groups(loadGroups(node.getConfigurationSection("groups")))
                .permissions(loadPermissions(node.getConfigurationSection("permissions")))
                .build();
    }

    private List<UserSection> loadUsers(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(this::loadUser).collect(Collectors.toList());
    }

    private UserSection loadUser(ConfigurationSection node) {
        return UserSection.builder()
                .username(node.getName())
                .password(node.getString("password"))
                .passwordType(UserSection.PasswordType.valueOf(node.getString("password_type", "PLAIN").toUpperCase()))
                .groups(node.getStringList("groups"))
                .build();
    }

    private List<GroupSection> loadGroups(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(this::loadGroup).collect(Collectors.toList());
    }

    private GroupSection loadGroup(ConfigurationSection node) {
        return GroupSection.builder()
                .name(node.getName())
                .permissions(node.getStringList("permissions"))
                .build();
    }

    private List<PermissionSection> loadPermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(this::loadPermission).collect(Collectors.toList());
    }

    private PermissionSection loadPermission(ConfigurationSection node) {
        return PermissionSection.builder()
                .name(node.getName())
                .namespaces(loadNamespacePermissions(node.getConfigurationSection("namespaces")))
                .classes(loadClassPermissions(node.getConfigurationSection("classes")))
                .streams(loadStreamPermission(node.getConfigurationSection("streams")))
                .build();
    }

    private List<NamespacePermissionSection> loadNamespacePermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(this::loadNamespacePermission).collect(Collectors.toList());
    }

    private NamespacePermissionSection loadNamespacePermission(ConfigurationSection node) {
        return NamespacePermissionSection.builder()
                .name(node.getName())
                .type(PermissionSection.Type.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .methods(node.getStringList("methods"))
                .build();
    }

    private List<ClassPermissionSection> loadClassPermissions(ConfigurationSection node) {
        return node.getKeys(false).stream().map(node::getConfigurationSection).map(this::loadClassPermission).collect(Collectors.toList());
    }

    private ClassPermissionSection loadClassPermission(ConfigurationSection node) {
        return ClassPermissionSection.builder()
                .name(node.getName())
                .type(PermissionSection.Type.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .methods(node.getStringList("methods"))
                .build();
    }

    private StreamPermissionSection loadStreamPermission(ConfigurationSection node) {
        return StreamPermissionSection.builder()
                .type(PermissionSection.Type.valueOf(node.getString("type", "BLACKLIST").toUpperCase()))
                .streams(node.getStringList("streams"))
                .build();
    }
}
