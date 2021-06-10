package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        this.command = "unmute";
        this.permission = "flame.unmute";
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

                    if (profile.getActivePunishmentByType(Punishment.Type.MUTE) == null) {
                        sender.sendMessage(CC.RED + "That player does not have an existing punishment.");
                        return;
                    }

                    Punishment punishment = profile.getActivePunishmentByType(Punishment.Type.MUTE);

                    punishment.setRemoved(true);
                    punishment.setRemovedAt(System.currentTimeMillis());
                    punishment.setRemovedReason(reason);

                    if (sender instanceof Player) {
                        punishment.setRemovedBy(((Player) sender).getUniqueId());
                    }

                    punishment.save();
                    profile.getCachedPunishments().remove(punishment);

                    profile.save();

                    Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

                    pigeon.broadcast(new PunishmentBroadcastPayload(punishment.getId(), profile.getUuid()));
                });
    }

}
