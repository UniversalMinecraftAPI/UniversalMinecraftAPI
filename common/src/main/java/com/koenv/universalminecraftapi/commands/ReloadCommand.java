package com.koenv.universalminecraftapi.commands;

import com.koenv.universalminecraftapi.ChatColor;
import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;

public class ReloadCommand extends Command {
    @Override
    public boolean onCommand(UniversalMinecraftAPIInterface uma, CommandSource commandSource, String[] args) {
        if (args.length < 1) {
            args = new String[]{"global"};
        }

        switch (args[0]) {
            case "global":
                if (!commandSource.hasPermission("universalminecraftapi.reload.global")) {
                    commandSource.sendMessage(ChatColor.RED, "No permission to do a global reload");
                    return false;
                }
                commandSource.sendMessage(ChatColor.GREEN, "Global reloaded (NOT IMPLEMENTED YET)");
                break;
            case "users":
                if (!commandSource.hasPermission("universalminecraftapi.reload.global")) {
                    commandSource.sendMessage(ChatColor.RED, "No permission to do a users reload");
                    return false;
                }

                try {
                    uma.getProvider().reloadUsers();
                    commandSource.sendMessage(ChatColor.GREEN, "Users reloaded:");
                    commandSource.sendMessage(ChatColor.GREEN, "Total users: " + uma.getUserManager().getUsers().size());
                    commandSource.sendMessage(ChatColor.GREEN, "Total groups: " + uma.getUserManager().getGroups().size());
                    commandSource.sendMessage(ChatColor.GREEN, "Total permissions: " + uma.getUserManager().getPermissions().size());

                } catch (Throwable t) {
                    t.printStackTrace();
                    commandSource.sendMessage(ChatColor.RED, "Reloading users failed: " + t.getMessage());
                }

                break;
            default:
                commandSource.sendMessage(ChatColor.RED, "Invalid reload specified");
                return false;
        }

        return true;
    }

    @Override
    public boolean hasPermission(CommandSource commandSource) {
        return commandSource.hasPermission("universalminecraftapi.reload");
    }

    @Override
    public String getDescription() {
        return "Reload a specific portion of UniversalMinecraftAPI";
    }

    @Override
    public String getUsage() {
        return "[global|users]";
    }
}
