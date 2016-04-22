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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.*;

public class CommandManagerTest {
    @Test
    public void testNoCommands() {
        createCommandManager()
                .handle(new TestCommandSource(ChatColor.RED, "Command not found"), new String[]{"non"});
    }

    @Test
    public void testEmptyCommand() {
        createCommandManager()
                .handle(new TestCommandSource(ChatColor.RED, "Invalid command."), new String[]{});
    }

    @Test
    public void testInvalidCommandWithHelp() {
        CommandManager manager = createCommandManager();
        manager.registerCommand("help", new TestHelpCommand());

        Queue<ImmutablePair<String, ChatColor>> messages = new LinkedBlockingQueue<>();
        messages.offer(ImmutablePair.of("No help found", ChatColor.AQUA));
        messages.offer(ImmutablePair.of("Command not found", ChatColor.RED));
        TestCommandSource source = new TestCommandSource(messages);

        manager.handle(source, new String[]{"non"});
    }

    @Test
    public void testHandledCommand() {
        CommandManager manager = createCommandManager();
        manager.registerCommand("help", new TestHelpCommand());
        assertTrue(manager.getCommands().containsKey("help"));

        Queue<ImmutablePair<String, ChatColor>> messages = new LinkedBlockingQueue<>();
        messages.offer(ImmutablePair.of("No help found", ChatColor.AQUA));
        TestCommandSource source = new TestCommandSource(messages);

        manager.handle(source, new String[]{"help"});
    }

    @Test
    public void testCommandWithNoPermission() {
        CommandManager manager = createCommandManager();
        manager.registerCommand("help", new TestHelpCommand());

        Queue<ImmutablePair<String, ChatColor>> messages = new LinkedBlockingQueue<>();
        messages.offer(ImmutablePair.of("You don't have permission.", ChatColor.RED));
        TestCommandSource source = new NoPermissionTestCommandSource(messages);

        manager.handle(source, new String[]{"help"});
    }

    private CommandManager createCommandManager() {
        return new CommandManager(new TestUniversalMinecraftAPI());
    }

    private static class TestCommandSource implements CommandSource {
        private Queue<ImmutablePair<String, ChatColor>> queue;

        public TestCommandSource(ChatColor expectedColor, String expectedMessage) {
            this.queue = new LinkedBlockingQueue<>();
            queue.offer(ImmutablePair.of(expectedMessage, expectedColor));
        }

        public TestCommandSource(Queue<ImmutablePair<String, ChatColor>> queue) {
            this.queue = queue;
        }

        @Override
        public void sendMessage(String text) {
            ImmutablePair<String, ChatColor> pair = queue.poll();
            if (pair == null) {
                fail("No more messages expected, but got " + text);
            }
            assertEquals(pair.getLeft(), text);
            assertNull(pair.getRight());
        }

        @Override
        public void sendMessage(ChatColor color, String text) {
            ImmutablePair<String, ChatColor> pair = queue.poll();
            if (pair == null) {
                fail("No more messages expected, but got " + text);
            }
            assertEquals(pair.getLeft(), text);
            assertEquals(pair.getRight(), color);
        }

        @Override
        public boolean hasPermission(String permission) {
            return true;
        }
    }

    private static class NoPermissionTestCommandSource extends TestCommandSource {
        public NoPermissionTestCommandSource(ChatColor expectedColor, String expectedMessage) {
            super(expectedColor, expectedMessage);
        }

        public NoPermissionTestCommandSource(Queue<ImmutablePair<String, ChatColor>> queue) {
            super(queue);
        }

        @Override
        public boolean hasPermission(String permission) {
            return false;
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

    private static class TestHelpCommand extends Command {

        @Override
        public void onCommand(UniversalMinecraftAPIInterface uma, CommandSource commandSource, String[] args) {
            commandSource.sendMessage(ChatColor.AQUA, "No help found");
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getUsage() {
            return null;
        }

        @Override
        public boolean hasPermission(CommandSource commandSource) {
            return commandSource.hasPermission("");
        }
    }
}
