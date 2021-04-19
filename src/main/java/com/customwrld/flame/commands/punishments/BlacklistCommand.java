package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BlacklistCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("blacklist")
                .withPermission("flame.blacklist")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    String reason = (String) args[1];

                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Config.CONSOLE_ONLY.format());
                        return;
                    }

                    if (profile.getActivePunishmentByType(PunishmentType.BLACKLIST) != null) {
                        sender.sendMessage(CC.RED + "That player already has an pre-existing punishment.");
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player) {
                        addedBy = (((Player) sender).getUniqueId());
                    }

                    Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BLACKLIST, addedBy, System.currentTimeMillis(), reason.replace("-s", ""), -1);

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