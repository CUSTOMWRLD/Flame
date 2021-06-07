package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class ClearPunishmentsCommand extends Command {

    public ClearPunishmentsCommand() {
        this.command = "clearhistory";
        this.permission = "flame.clearhistory";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
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
