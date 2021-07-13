package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BlacklistCommand extends Command {

    public BlacklistCommand() {
        this.command = "blacklist";
        this.permission = "flame.blacklist";
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

                    if (profile.api().getActivePunishmentByType(Punishment.Type.BLACKLIST) != null) {
                        sender.sendMessage(Component.text("That player already has an pre-existing punishment.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player player) {
                        addedBy = player.getUniqueId();
                    }

                    Punishment punishment = new Punishment(
                            profile.getId(),
                            Punishment.Type.BLACKLIST,
                            addedBy,
                            System.currentTimeMillis(),
                            reason,
                            -1L
                    );

                    profile.api().addPunishment(punishment);
                });
    }

}