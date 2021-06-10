package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Iterator;

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

                    Iterator<Punishment> iterator = profile.getCachedPunishments().iterator();

                    while (iterator.hasNext()) {
                        Punishment punishment = iterator.next();

                        punishment.delete();
                        iterator.remove();
                    }

                    profile.getPunishments().clear();
                    profile.save();

                    sender.sendMessage(CC.GREEN + "Cleared punishments for " + profile.getName());
                });
    }

}
