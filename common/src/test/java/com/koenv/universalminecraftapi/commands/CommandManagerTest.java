package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIProvider;
import com.koenv.universalminecraftapi.config.UniversalMinecraftAPIRootConfiguration;
import com.koenv.universalminecraftapi.http.RequestHandler;
import com.koenv.universalminecraftapi.methods.MethodInvoker;
import com.koenv.universalminecraftapi.parser.ExpressionParser;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.streams.StreamManager;
import com.koenv.universalminecraftapi.users.UserManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommandManagerTest {
    @Test
    public void testNoCommands() {
        CommandManager commandManager = new CommandManager(new TestUniversalMinecraftAPI());
        commandManager.handle(new TestCommandSource(ChatColor.RED, "Command not found"), new String[]{"non"});
    }

    private static class TestCommandSource implements CommandSource {
        private String expectedMessage;
        private ChatColor expectedColor;

        public TestCommandSource(ChatColor expectedColor, String expectedMessage) {
            this.expectedColor = expectedColor;
            this.expectedMessage = expectedMessage;
        }

        @Override
        public void sendMessage(String text) {
            assertEquals(expectedMessage, text);
            assertNull(expectedColor);
        }

        @Override
        public void sendMessage(ChatColor color, String text) {
            assertEquals(expectedMessage, text);
            assertEquals(expectedColor, color);
        }

        @Override
        public boolean hasPermission(String permission) {
            return true;
        }
    }

    private static class TestUniversalMinecraftAPI implements UniversalMinecraftAPIInterface {

        @Override
        public void setup(UniversalMinecraftAPIRootConfiguration configuration) {

        }

        @Override
        public void destroy() {

        }

        @Override
        public UniversalMinecraftAPIProvider getProvider() {
            return null;
        }

        @Override
        public ExpressionParser getExpressionParser() {
            return null;
        }

        @Override
        public MethodInvoker getMethodInvoker() {
            return null;
        }

        @Override
        public CommandManager getCommandManager() {
            return null;
        }

        @Override
        public SerializerManager getSerializerManager() {
            return null;
        }

        @Override
        public UniversalMinecraftAPIRootConfiguration getConfiguration() {
            return null;
        }

        @Override
        public RequestHandler getRequestHandler() {
            return null;
        }

        @Override
        public StreamManager getStreamManager() {
            return null;
        }

        @Override
        public UserManager getUserManager() {
            return null;
        }
    }
}
