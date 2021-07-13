package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.flame.Config;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                    profile.update();

                    sender.sendMessage(Component.text("Cleared all punishments for ")
                            .color(NamedTextColor.GREEN)
                            .append(FlameAPI.getColoredName(profile)));
                });
    }

}
