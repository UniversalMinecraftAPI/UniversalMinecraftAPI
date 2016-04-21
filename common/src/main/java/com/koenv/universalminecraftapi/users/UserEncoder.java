package com.koenv.universalminecraftapi.users;

public interface UserEncoder {
    boolean supports(EncryptionContext context);

    boolean checkCredentials(String enteredPassword, EncryptionContext context);
}
