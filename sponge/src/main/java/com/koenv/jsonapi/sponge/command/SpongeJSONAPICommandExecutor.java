package com.koenv.jsonapi.sponge.command;

import com.koenv.jsonapi.sponge.SpongeCommandSource;
import com.koenv.jsonapi.sponge.SpongeJSONAPI;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class SpongeJSONAPICommandExecutor implements CommandExecutor {
    private SpongeJSONAPI plugin;

    public SpongeJSONAPICommandExecutor(SpongeJSONAPI plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) throws CommandException {
        String[] arguments = args.<String>getOne("arguments").get().split("\\s");
        plugin.getJSONAPI().getCommandManager().handle(new SpongeCommandSource(src), arguments);
        return CommandResult.empty();
    }
}
