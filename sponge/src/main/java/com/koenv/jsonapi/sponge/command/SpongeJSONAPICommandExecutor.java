package com.koenv.jsonapi.sponge.command;

import com.koenv.jsonapi.sponge.SpongeCommandSource;
import com.koenv.jsonapi.sponge.SpongeJSONAPI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpongeJSONAPICommandExecutor {
    private SpongeJSONAPI plugin;

    public SpongeJSONAPICommandExecutor(SpongeJSONAPI plugin) {
        this.plugin = plugin;
    }

    public CommandResult executeExecCommand(CommandSource source, CommandContext arguments) throws CommandException {
        List<String> args = new ArrayList<>();
        args.add("exec");
        args.addAll(Arrays.asList(arguments.<String>getOne("expression").orElse("").split(" ")));
        plugin.getJSONAPI().getCommandManager().handle(new SpongeCommandSource(source), args.toArray(new String[args.size()]));
        return CommandResult.success();
    }

    public CommandResult executeCreateApiDocCommand(CommandSource source, CommandContext arguments) {
        List<String> args = new ArrayList<>();
        args.add("createapidoc");
        args.add(arguments.<String>getOne("file").orElse(""));
        if (arguments.hasAny("format")) {
            args.add(arguments.<String>getOne("format").orElse(""));
        }
        plugin.getJSONAPI().getCommandManager().handle(new SpongeCommandSource(source), args.toArray(new String[args.size()]));
        return CommandResult.success();
    }
}
