package com.koenv.jsonapi.users.impl;

import com.koenv.jsonapi.users.EncryptionContext;
import com.koenv.jsonapi.users.model.User;

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
