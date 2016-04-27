package com.koenv.universalminecraftapi.users;

import com.koenv.universalminecraftapi.config.user.UsersConfiguration;
import com.koenv.universalminecraftapi.http.websocket.APIKeyManager;
import com.koenv.universalminecraftapi.permissions.PermissionTree;
import com.koenv.universalminecraftapi.users.impl.EncryptionContextImpl;
import com.koenv.universalminecraftapi.users.model.Group;
import com.koenv.universalminecraftapi.users.model.Permission;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserManager {
    private Map<String, User> users;
    private Map<String, Group> groups;

    private List<UserEncoder> encoders = new ArrayList<>();

    private APIKeyManager apiKeyManager;

    public UserManager() {
        this.apiKeyManager = new APIKeyManager();
    }

    public void loadConfiguration(UsersConfiguration configuration) {
        this.groups = configuration.getGroups().stream().map(groupSection -> Group.builder()
                .name(groupSection.getName())
                .defaultPermission(groupSection.getDefaultPermission())
                .inheritsFrom(groupSection.getInheritsFrom())
                .permissions(
                        groupSection.getPermissions().stream().map(permissionSection ->
                                Permission.builder()
                                        .name(permissionSection.getName())
                                        .value(permissionSection.getValue())
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build()
        ).collect(Collectors.toMap(Group::getName, Function.identity()));

        this.users = configuration.getUsers().stream().map(userSection -> {
                    List<Group> groups = userSection.getGroups()
                            .stream()
                            .map(s -> this.groups.get(s))
                            .flatMap(group -> {
                                HashSet<Group> set = new HashSet<>();
                                set.add(group);
                                set.addAll(group.getInheritsFrom().stream().map(s -> this.groups.get(s)).collect(Collectors.toList()));
                                return set.stream();
                            })
                            .collect(Collectors.toList());
                    return User.builder()
                            .username(userSection.getUsername())
                            .password(userSection.getPassword())
                            .passwordType(userSection.getPasswordType())
                            .groups(groups)
                            .permissions(
                                    PermissionTree.of(
                                            groups.stream()
                                                    .flatMap(group ->
                                                            group.getPermissions()
                                                                    .stream()
                                                                    .map(permission -> Pair.of(permission.getName(), permission.getValue()))
                                                    )
                                                    .collect(Collectors.toList()),
                                            groups.stream().map(Group::getDefaultPermission).reduce((result, i) -> result + i).orElse(0)
                                    )
                            )
                            .build();
                }
        ).collect(Collectors.toMap(User::getUsername, Function.identity()));
    }

    public Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public Collection<Group> getGroups() {
        return Collections.unmodifiableCollection(groups.values());
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
            throw new IllegalStateException("No encoders have been registered");
        }

        for (UserEncoder encoder : supportedEncoders) {
            if (encoder.checkCredentials(password, context)) {
                return true;
            }
        }

        return false;
    }

    public APIKeyManager getApiKeyManager() {
        return apiKeyManager;
    }
}
