package com.koenv.universalminecraftapi.sponge;

import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.config.WebServerSection;
import com.koenv.universalminecraftapi.config.WebServerSecureSection;
import com.koenv.universalminecraftapi.config.WebServerThreadPoolSection;
import com.koenv.universalminecraftapi.config.user.*;
import com.koenv.universalminecraftapi.users.model.PermissionType;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.List;
import java.util.stream.Collectors;

public final class SpongeConfigurationLoader {
    private SpongeConfigurationLoader() {

    }

    public static UniversalMinecraftAPIRootConfiguration loadRoot(CommentedConfigurationNode node) {
        return UniversalMinecraftAPIRootConfiguration.builder()
                .webServer(loadWebServer(node.getNode("web_server")))
                .build();
    }

    public static WebServerSection loadWebServer(CommentedConfigurationNode node) {
        return WebServerSection.builder()
                .ipAddress(node.getNode("ip_address").getString())
                .port(node.getNode("port").getInt(-1))
                .secure(loadWebServerSecure(node.getNode("secure")))
                .threadPool(loadWebServerThreadPool(node.getNode("thread_pool")))
                .build();
    }

    private static WebServerSecureSection loadWebServerSecure(CommentedConfigurationNode node) {
        return WebServerSecureSection.builder()
                .enabled(node.getNode("enabled").getBoolean())
                .keyStoreFile(node.getNode("keystore", "file").getString())
                .keystorePassword(node.getNode("keystore", "password").getString())
                .trustStoreFile(node.getNode("truststore", "file").getString())
                .trustStorePassword(node.getNode("truststore", "password").getString())
                .build();
    }

    private static WebServerThreadPoolSection loadWebServerThreadPool(CommentedConfigurationNode node) {
        return WebServerThreadPoolSection.builder()
                .maxThreads(node.getNode("max_threads").getInt(-1))
                .minThreads(node.getNode("min_threads").getInt(-1))
                .idleTimeoutMillis(node.getNode("idle_timeout").getInt(-1))
                .build();
    }

    public static UsersConfiguration loadUsersConfiguration(CommentedConfigurationNode node) {
        return UsersConfiguration.builder()
                .users(loadUsers(node.getNode("users")))
                .groups(loadGroups(node.getNode("groups")))
                .permissions(loadPermissions(node.getNode("permissions")))
                .build();
    }

    private static List<UserSection> loadUsers(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(SpongeConfigurationLoader::loadUser).collect(Collectors.toList());
    }

    private static UserSection loadUser(CommentedConfigurationNode node) {
        return UserSection.builder()
                .username(node.getNode("username").getString())
                .password(node.getNode("password").getString())
                .passwordType(node.getNode("password_type").getString("plain"))
                .groups(loadStringList(node.getNode("groups")))
                .build();
    }

    private static List<GroupSection> loadGroups(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(SpongeConfigurationLoader::loadGroup).collect(Collectors.toList());
    }

    private static GroupSection loadGroup(CommentedConfigurationNode node) {
        return GroupSection.builder()
                .name(node.getNode("name").getString())
                .permissions(loadStringList(node.getNode("permissions")))
                .build();
    }

    private static List<PermissionSection> loadPermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(SpongeConfigurationLoader::loadPermission).collect(Collectors.toList());
    }

    private static PermissionSection loadPermission(CommentedConfigurationNode node) {
        return PermissionSection.builder()
                .name(node.getNode("name").getString())
                .namespaces(loadNamespacePermissions(node.getNode("namespaces")))
                .classes(loadClassPermissions(node.getNode("classes")))
                .streams(loadStreamPermission(node.getNode("streams")))
                .build();
    }

    private static List<NamespacePermissionSection> loadNamespacePermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(SpongeConfigurationLoader::loadNamespacePermission).collect(Collectors.toList());
    }

    private static NamespacePermissionSection loadNamespacePermission(CommentedConfigurationNode node) {
        return NamespacePermissionSection.builder()
                .name(node.getNode("name").getString())
                .type(PermissionType.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .methods(loadStringList(node.getNode("methods")))
                .build();
    }

    private static List<ClassPermissionSection> loadClassPermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(SpongeConfigurationLoader::loadClassPermission).collect(Collectors.toList());
    }

    private static ClassPermissionSection loadClassPermission(CommentedConfigurationNode node) {
        return ClassPermissionSection.builder()
                .name(node.getNode("name").getString())
                .type(PermissionType.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .methods(loadStringList(node.getNode("methods")))
                .build();
    }

    private static StreamPermissionSection loadStreamPermission(CommentedConfigurationNode node) {
        return StreamPermissionSection.builder()
                .type(PermissionType.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .streams(loadStringList(node.getNode("streams")))
                .build();
    }

    private static List<String> loadStringList(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(CommentedConfigurationNode::getString).collect(Collectors.toList());
    }
}
