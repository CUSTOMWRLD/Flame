package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.command = "unban";
        this.permission = "flame.unban";
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

                    Punishment punishment = profile.api().getActivePunishmentByType(Punishment.Type.BAN);

                    if (punishment == null) {
                        sender.sendMessage(Component.text("That player does not have an existing punishment.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    UUID remover = null;

                    if (sender instanceof Player) {
                        remover = ((Player) sender).getUniqueId();
                    }

                    profile.api().removePunishment(punishment, remover, System.currentTimeMillis(), reason);
                });
    }
}
