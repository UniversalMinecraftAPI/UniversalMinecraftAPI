package com.koenv.jsonapi.spigot;

import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSection;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SpigotConfigurationLoader {
    public JSONAPIConfiguration load(FileConfiguration config) {
        return JSONAPIConfiguration.builder()
                .webServer(loadWebServer(config.getConfigurationSection("web_server")))
                .build();
    }

    public WebServerSection loadWebServer(ConfigurationSection section) {
        return WebServerSection.builder()
                .ipAddress(section.getString("ip_address"))
                .port(section.getInt("port", -1))
                .secure(loadWebServerSecure(section.getConfigurationSection("secure")))
                .threadPool(loadWebServerThreadPool(section.getConfigurationSection("thread_pool")))
                .build();
    }

    private WebServerSecureSection loadWebServerSecure(ConfigurationSection node) {
        return WebServerSecureSection.builder()
                .enabled(node.getBoolean("enabled"))
                .keyStoreFile(node.getString("keystore.file"))
                .keystorePassword(node.getString("keystore.password"))
                .trustStoreFile(node.getString("truststore.file"))
                .trustStorePassword(node.getString("truststore.password"))
                .build();
    }

    private WebServerThreadPoolSection loadWebServerThreadPool(ConfigurationSection node) {
        return WebServerThreadPoolSection.builder()
                .maxThreads(node.getInt("max_threads", -1))
                .minThreads(node.getInt("min_threads", -1))
                .idleTimeoutMillis(node.getInt("idle_timeout", -1))
                .build();
    }
}
