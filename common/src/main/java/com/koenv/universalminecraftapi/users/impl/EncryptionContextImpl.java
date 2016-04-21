package com.koenv.universalminecraftapi.users.impl;

import com.koenv.universalminecraftapi.users.EncryptionContext;
import com.koenv.universalminecraftapi.users.model.User;

public class EncryptionContextImpl implements EncryptionContext {
    private final User user;

    public EncryptionContextImpl(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
