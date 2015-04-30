package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.ChatColor;
import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.parser.ExpressionParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommandManagerTest {
    @Test
    public void testNoCommands() {
        CommandManager commandManager = new CommandManager(new TestJSONAPI());
        commandManager.handle(new TestCommandSource(ChatColor.RED, "Command not found"), new String[]{"non"});
    }

    private static class TestCommandSource implements CommandSource {
        private String expectedMessage;
        private ChatColor expectedColor;

        public TestCommandSource(String expectedMessage) {
            this.expectedMessage = expectedMessage;
        }

        public TestCommandSource(String expectedMessage, ChatColor expectedColor) {
            this.expectedMessage = expectedMessage;
            this.expectedColor = expectedColor;
        }

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
    }

    private static class TestJSONAPI implements JSONAPIInterface {

        @Override
        public void setup() {

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
    }
}
