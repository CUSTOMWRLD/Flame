package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.util.Duration;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("mute")
                .withPermission("flame.mute")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(Arguments.durationArgument("duration"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    Duration duration = (Duration) args[1];
                    String reason = (String) args[2];

                    if (profile.getActivePunishmentByType(PunishmentType.MUTE) != null) {
                        sender.sendMessage(CC.RED + "That player already has an pre-existing punishment.");
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player) {
                        addedBy = (((Player) sender).getUniqueId());
                    }

                    Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.MUTE, addedBy, System.currentTimeMillis(), reason.replace("-s", ""), duration.getValue());

                    profile.getPunishments().add(punishment);
                    profile.save();

                    Player player = profile.getPlayer();

                    if (player != null) {
                        player.sendMessage(CC.RED + "You have been " + punishment.getContext() + " for: " + punishment.getAddedReason());

                        if (!punishment.isPermanent()) {
                            player.sendMessage(CC.RED + "This mute will expire in " + TimeUtil.millisToRoundedTime(duration.getValue()) + ".");
                        }

                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 0.1F);
                    }

                    boolean silent = reason.contains("-s");

                    if (sender.hasPermission("flame.bypasssilent")) {
                        new PunishmentBroadcastPayload(punishment, profile.getUuid(), silent).send();
                    } else {
                        new PunishmentBroadcastPayload(punishment, profile.getUuid(), true).send();
                    }

                    punishment.sendEmbed(profile);
                });
    }

}
