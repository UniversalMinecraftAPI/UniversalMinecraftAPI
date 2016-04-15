package com.koenv.jsonapi.users.encoders;

import com.koenv.jsonapi.users.EncryptionContext;
import com.koenv.jsonapi.users.UserEncoder;

import java.util.Objects;

public class PlainTextEncoder implements UserEncoder {
    public static final String PASSWORD_TYPE = "plain";

    @Override
    public boolean supports(EncryptionContext context) {
        return Objects.equals(context.getUser().getPasswordType(), PASSWORD_TYPE);
    }

    @Override
    public boolean checkCredentials(String enteredPassword, EncryptionContext context) {
        return Objects.equals(enteredPassword, context.getUser().getPassword());
    }
}
