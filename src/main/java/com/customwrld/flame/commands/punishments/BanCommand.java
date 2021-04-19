package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.util.Duration;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("ban")
                .withPermission("flame.ban")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(Arguments.durationArgument("duration"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    Duration duration = (Duration) args[1];
                    String reason = (String) args[2];

                    if (profile.getActivePunishmentByType(PunishmentType.BAN) != null) {
                        sender.sendMessage(CC.RED + "That player already has an pre-existing punishment.");
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player) {
                        addedBy = (((Player) sender).getUniqueId());
                    }

                    Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BAN, addedBy, System.currentTimeMillis(), reason.replace("-s", ""), duration.getValue());

                    profile.getPunishments().add(punishment);
                    profile.save();

                    Player player = profile.getPlayer();

                    if (player != null) {
                        player.kickPlayer(String.join("\n", punishment.getKickMessage()));
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
