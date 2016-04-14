package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSection;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import com.koenv.jsonapi.config.user.*;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.List;
import java.util.stream.Collectors;

public class SpongeConfigurationLoader {
    public JSONAPIConfiguration load(CommentedConfigurationNode node, CommentedConfigurationNode usersNode) {
        return JSONAPIConfiguration.builder()
                .webServer(loadWebServer(node.getNode("web_server")))
                .usersConfiguration(loadUsersConfiguration(usersNode))
                .build();
    }

    public WebServerSection loadWebServer(CommentedConfigurationNode node) {
        return WebServerSection.builder()
                .ipAddress(node.getNode("ip_address").getString())
                .port(node.getNode("port").getInt(-1))
                .secure(loadWebServerSecure(node.getNode("secure")))
                .threadPool(loadWebServerThreadPool(node.getNode("thread_pool")))
                .build();
    }

    private WebServerSecureSection loadWebServerSecure(CommentedConfigurationNode node) {
        return WebServerSecureSection.builder()
                .enabled(node.getNode("enabled").getBoolean())
                .keyStoreFile(node.getNode("keystore", "file").getString())
                .keystorePassword(node.getNode("keystore", "password").getString())
                .trustStoreFile(node.getNode("truststore", "file").getString())
                .trustStorePassword(node.getNode("truststore", "password").getString())
                .build();
    }

    private WebServerThreadPoolSection loadWebServerThreadPool(CommentedConfigurationNode node) {
        return WebServerThreadPoolSection.builder()
                .maxThreads(node.getNode("max_threads").getInt(-1))
                .minThreads(node.getNode("min_threads").getInt(-1))
                .idleTimeoutMillis(node.getNode("idle_timeout").getInt(-1))
                .build();
    }

    private UsersConfiguration loadUsersConfiguration(CommentedConfigurationNode node) {
        return UsersConfiguration.builder()
                .users(loadUsers(node.getNode("users")))
                .groups(loadGroups(node.getNode("groups")))
                .permissions(loadPermissions(node.getNode("permissions")))
                .build();
    }

    private List<UserSection> loadUsers(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(this::loadUser).collect(Collectors.toList());
    }

    private UserSection loadUser(CommentedConfigurationNode node) {
        return UserSection.builder()
                .username(node.getNode("username").getString())
                .password(node.getNode("password").getString())
                .passwordType(UserSection.PasswordType.valueOf(node.getNode("password_type").getString("PLAIN").toUpperCase()))
                .groups(loadStringList(node.getNode("groups")))
                .build();
    }

    private List<GroupSection> loadGroups(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(this::loadGroup).collect(Collectors.toList());
    }

    private GroupSection loadGroup(CommentedConfigurationNode node) {
        return GroupSection.builder()
                .name(node.getNode("name").getString())
                .permissions(loadStringList(node.getNode("permissions")))
                .build();
    }

    private List<PermissionSection> loadPermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(this::loadPermission).collect(Collectors.toList());
    }

    private PermissionSection loadPermission(CommentedConfigurationNode node) {
        return PermissionSection.builder()
                .name(node.getNode("name").getString())
                .namespaces(loadNamespacePermissions(node.getNode("namespaces")))
                .classes(loadClassPermissions(node.getNode("classes")))
                .streams(loadStreamPermission(node.getNode("streams")))
                .build();
    }

    private List<NamespacePermissionSection> loadNamespacePermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(this::loadNamespacePermission).collect(Collectors.toList());
    }

    private NamespacePermissionSection loadNamespacePermission(CommentedConfigurationNode node) {
        return NamespacePermissionSection.builder()
                .name(node.getNode("name").getString())
                .type(PermissionSection.Type.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .methods(loadStringList(node.getNode("methods")))
                .build();
    }

    private List<ClassPermissionSection> loadClassPermissions(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(this::loadClassPermission).collect(Collectors.toList());
    }

    private ClassPermissionSection loadClassPermission(CommentedConfigurationNode node) {
        return ClassPermissionSection.builder()
                .name(node.getNode("name").getString())
                .type(PermissionSection.Type.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .methods(loadStringList(node.getNode("methods")))
                .build();
    }

    private StreamPermissionSection loadStreamPermission(CommentedConfigurationNode node) {
        return StreamPermissionSection.builder()
                .type(PermissionSection.Type.valueOf(node.getNode("type").getString("BLACKLIST").toUpperCase()))
                .streams(loadStringList(node.getNode("streams")))
                .build();
    }

    private List<String> loadStringList(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(CommentedConfigurationNode::getString).collect(Collectors.toList());
    }
}
