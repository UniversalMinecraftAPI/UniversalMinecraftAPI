package com.koenv.universalminecraftapi.http.websocket;

import com.koenv.universalminecraftapi.users.model.User;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class APIKeyManager {
    private List<APIKey> keys = new CopyOnWriteArrayList<>();

    /**
     * Generate a fairly random API key valid for 1 minute
     *
     * @param user The user for which this API key is generated
     * @return The API key
     */
    public String generate(User user) {
        if (user == null) {
            throw new NullPointerException("user == null");
        }

        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[128 / 8];
        random.nextBytes(bytes);
        String key = DatatypeConverter.printHexBinary(bytes).toLowerCase();

        keys.add(new APIKey(key, System.currentTimeMillis(), user));

        return key;
    }

    public User getByAPIKey(String key) {
        return keys.stream()
                .filter(apiKey -> Objects.equals(apiKey.key, key))
                .filter(apiKey -> System.currentTimeMillis() - apiKey.added < 60000)
                .findFirst()
                .map(apiKey -> apiKey.user)
                .orElse(null);
    }

    public void cleanup() {
        keys.removeAll(keys.stream().filter(apiKey -> System.currentTimeMillis() - apiKey.added > 60000).collect(Collectors.toList()));
    }

    public static class APIKey {
        private String key;
        private long added;
        private User user;

        public APIKey(String key, long added, User user) {
            this.key = key;
            this.added = added;
            this.user = user;
        }
    }
}
