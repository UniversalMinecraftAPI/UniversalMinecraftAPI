package com.koenv.universalminecraftapi.sponge;

import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.config.WebServerSection;
import com.koenv.universalminecraftapi.config.WebServerSecureSection;
import com.koenv.universalminecraftapi.config.WebServerThreadPoolSection;
import com.koenv.universalminecraftapi.config.user.GroupSection;
import com.koenv.universalminecraftapi.config.user.PermissionSection;
import com.koenv.universalminecraftapi.config.user.UserSection;
import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                .defaultPermission(node.getNode("default-permission").getInt(0))
                .inheritsFrom(loadStringList(node.getNode("inherits-from")))
                .permissions(loadPermissions(node.getNode("permissions")))
                .build();
    }

    private static List<PermissionSection> loadPermissions(CommentedConfigurationNode node) {
        List<PermissionSection> permissionSections = new ArrayList<>();
        node.getChildrenMap().forEach((key, value) -> {
            loadPermissionAndAdd(permissionSections, key, value);
        });
        return permissionSections;
    }

    private static void loadPermissionAndAdd(List<PermissionSection> permissionSections, Object key, CommentedConfigurationNode value) {
        if (value.hasMapChildren()) {
            value.getChildrenMap().forEach((childKey, childValue) -> {
                if (Objects.equals(childKey, "default")) {
                    loadPermissionAndAdd(permissionSections, key, childValue);
                    return;
                }
                loadPermissionAndAdd(permissionSections, key + "." + childKey, childValue);
            });
            return;
        }
        permissionSections.add(
                PermissionSection.builder()
                        .name(key.toString())
                        .value(value.getInt(0))
                        .build()
        );
    }

    private static List<String> loadStringList(CommentedConfigurationNode node) {
        return node.getChildrenList().stream().map(CommentedConfigurationNode::getString).collect(Collectors.toList());
    }
}
