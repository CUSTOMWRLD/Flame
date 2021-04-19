package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class ClearPunishmentsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("clearhistory")
                .withPermission(CommandPermission.OP)
                .withArguments(Arguments.profileArgument("player"))
                .withAliases("clearhis", "clearpunishments")
                .executes((sender, args) -> {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Config.CONSOLE_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    profile.getPunishments().clear();
                    profile.save();

                    sender.sendMessage(ChatColor.GREEN + "Cleared punishments for " + profile.getName() + ChatColor.GREEN + "!");
                });
    }

}
