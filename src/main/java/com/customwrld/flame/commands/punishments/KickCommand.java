package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("kick")
                .withPermission("flame.kick")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    String reason = (String) args[1];

                    Player player = profile.getPlayer();

                    if (player == null) {
                        sender.sendMessage(CC.RED + "That player is not online.");
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player) {
                        addedBy = (((Player) sender).getUniqueId());
                    }

                    Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.KICK, addedBy, System.currentTimeMillis(), reason.replace("-s", ""), -1);

                    profile.getPunishments().add(punishment);
                    profile.save();

                    player.kickPlayer(String.join("\n", punishment.getKickMessage()));

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
