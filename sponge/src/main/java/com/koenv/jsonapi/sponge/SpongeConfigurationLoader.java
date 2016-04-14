package com.koenv.jsonapi.sponge;

import com.koenv.jsonapi.config.JSONAPIConfiguration;
import com.koenv.jsonapi.config.WebServerSection;
import com.koenv.jsonapi.config.WebServerSecureSection;
import com.koenv.jsonapi.config.WebServerThreadPoolSection;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class SpongeConfigurationLoader {
    public JSONAPIConfiguration load(CommentedConfigurationNode node) {
        return JSONAPIConfiguration.builder()
                .webServer(loadWebServer(node.getNode("web_server")))
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
}
