package com.koenv.jsonapi.users;

import com.koenv.jsonapi.config.user.UsersConfiguration;
import com.koenv.jsonapi.users.impl.EncryptionContextImpl;
import com.koenv.jsonapi.users.model.Group;
import com.koenv.jsonapi.users.model.Permission;
import com.koenv.jsonapi.users.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserManager {
    private Map<String, User> users;
    private Map<String, Group> groups;
    private Map<String, Permission> permissions;

    private List<UserEncoder> encoders = new ArrayList<>();

    public void loadConfiguration(UsersConfiguration configuration) {
        this.permissions = configuration.getPermissions().stream().map(permissionSection -> Permission.builder()
                .name(permissionSection.getName())
                .namespaces(
                        permissionSection.getNamespaces().stream().map(section -> Permission.NamespacePermissions.builder()
                                .name(section.getName())
                                .type(section.getType())
                                .methods(section.getMethods())
                                .build()
                        ).collect(Collectors.toList()))
                .classes(
                        permissionSection.getClasses().stream().map(section -> Permission.ClassPermissions.builder()
                                .name(section.getName())
                                .type(section.getType())
                                .methods(section.getMethods())
                                .build()
                        ).collect(Collectors.toList())
                )
                .streams(
                        Permission.StreamPermissions.builder()
                                .type(permissionSection.getStreams().getType())
                                .streams(permissionSection.getStreams().getStreams())
                                .build()
                )
                .build()
        ).collect(Collectors.toMap(Permission::getName, Function.identity()));

        this.groups = configuration.getGroups().stream().map(groupSection -> Group.builder()
                .name(groupSection.getName())
                .permissions(groupSection.getPermissions().stream().map(s -> permissions.get(s)).collect(Collectors.toList()))
                .build()
        ).collect(Collectors.toMap(Group::getName, Function.identity()));

        this.users = configuration.getUsers().stream().map(userSection -> User.builder()
                .username(userSection.getUsername())
                .password(userSection.getPassword())
                .passwordType(userSection.getPasswordType())
                .groups(userSection.getGroups().stream().map(s -> groups.get(s)).collect(Collectors.toList()))
                .build()
        ).collect(Collectors.toMap(User::getUsername, Function.identity()));
    }

    public Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public Collection<Group> getGroups() {
        return Collections.unmodifiableCollection(groups.values());
    }

    public Collection<Permission> getPermissions() {
        return Collections.unmodifiableCollection(permissions.values());
    }

    public Optional<User> getUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public void registerEncoder(UserEncoder encoder) {
        this.encoders.add(encoder);
    }

    public boolean checkCredentials(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }

        EncryptionContext context = new EncryptionContextImpl(user);

        List<UserEncoder> supportedEncoders = encoders.stream().filter(userEncoder -> userEncoder.supports(context)).collect(Collectors.toList());

        if (supportedEncoders.size() < 1) {
            return false;
        }

        for (UserEncoder encoder : supportedEncoders) {
            if (encoder.checkCredentials(password, context)) {
                return true;
            }
        }

        return false;
    }
}
