package com.koenv.universalminecraftapi.sponge.command;

import com.koenv.universalminecraftapi.sponge.SpongeCommandSource;
import com.koenv.universalminecraftapi.sponge.SpongeUniversalMinecraftAPI;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Optional;

public class SpongeUMACommandExecutor implements CommandExecutor {
    private SpongeUniversalMinecraftAPI plugin;

    public SpongeUMACommandExecutor(SpongeUniversalMinecraftAPI plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) throws CommandException {
        Optional<String> optional = args.<String>getOne("arguments");
        String[] arguments;
        if (optional.isPresent()) {
            arguments = optional.get().split("\\s");
        } else {
            arguments = new String[]{};
        }

        plugin.getUMA().getCommandManager().handle(new SpongeCommandSource(src), arguments);
        return CommandResult.empty();
    }
}
