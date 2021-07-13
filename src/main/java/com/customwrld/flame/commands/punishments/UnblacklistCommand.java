package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnblacklistCommand extends Command {

    public UnblacklistCommand() {
        this.command = "unblacklist";
        this.permission = "flame.unblacklist";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    String reason = (String) args[1];

                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Config.CONSOLE_ONLY.format());
                        return;
                    }

                    Punishment punishment = profile.api().getActivePunishmentByType(Punishment.Type.BLACKLIST);

                    if (punishment == null) {
                        sender.sendMessage(Component.text("That player does not have an existing punishment.")
                                .color(MC.CC.RED.getTextColor()));
                        return;
                    }

                    UUID remover = null;

                    if (sender instanceof Player player) {
                        remover = player.getUniqueId();
                    }

                    profile.api().removePunishment(punishment, remover, System.currentTimeMillis(), reason);
                });
    }

}
