package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Duration;
import com.customwrld.flame.util.Util;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand extends Command {

    public MuteCommand() {
        this.command = "mute";
        this.permission = "flame.mute";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(Arguments.durationArgument("duration"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    Duration duration = (Duration) args[1];
                    String reason = (String) args[2];

                    UUID addedBy = null;

                    if (sender instanceof Player executor) {
                        if (Util.isElevatedStaff(profile.getId())) {
                            executor.sendMessage(Component.text("You cannot use that command in this context.")
                                    .color(MC.CC.RED.getTextColor()));
                            return;
                        }

                        addedBy = executor.getUniqueId();
                    }

                    if (profile.api().getActivePunishmentByType(Punishment.Type.MUTE) != null) {
                        sender.sendMessage(Component.text("That player already has a pre-existing punishment.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    Punishment punishment = new Punishment(
                            profile.getId(),
                            Punishment.Type.MUTE,
                            addedBy,
                            System.currentTimeMillis(),
                            reason,
                            duration.getValue()
                    );

                    profile.api().addPunishment(punishment);
                });
    }
}
