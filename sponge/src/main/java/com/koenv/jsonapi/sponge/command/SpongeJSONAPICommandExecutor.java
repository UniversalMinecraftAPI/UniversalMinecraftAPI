package com.koenv.jsonapi.sponge.command;

import com.koenv.jsonapi.sponge.SpongeCommandSource;
import com.koenv.jsonapi.sponge.SpongeJSONAPI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpongeJSONAPICommandExecutor implements CommandExecutor {
    private SpongeJSONAPI plugin;

    public SpongeJSONAPICommandExecutor(SpongeJSONAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        String[] args = arguments.<String>getOne("arguments").orElse("").split(" ");
        if (args.length < 1) {
            source.sendMessage(Text.builder("This plugin needs at least 1 parameter.").color(TextColors.RED).build());
            return CommandResult.empty();
        }
        plugin.getJSONAPI().getCommandManager().handle(new SpongeCommandSource(source), args);
        return CommandResult.empty();
    }
}
