package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Duration;
import com.customwrld.flame.util.Util;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand extends Command {

    public BanCommand() {
        this.command = "ban";
        this.permission = "flame.ban";
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

                    if (sender instanceof Player) {
                        Player executor = (Player) sender;

                        if (Util.isElevatedStaff(executor, profile.getUuid())) {
                            return;
                        }

                        addedBy = executor.getUniqueId();
                    }

                    if (profile.getActivePunishmentByType(Punishment.Type.BAN) != null) {
                        sender.sendMessage(CC.RED + "That player already has an pre-existing punishment.");
                        return;
                    }

                    Punishment punishment = new Punishment(
                            Util.generateId(),
                            profile.getUuid(),
                            Punishment.Type.BAN,
                            addedBy,
                            System.currentTimeMillis(),
                            reason,
                            duration.getValue()
                    );

                    punishment.save();

                    profile.getCachedPunishments().add(punishment);
                    profile.getPunishments().add(punishment.getId());

                    profile.save();

                    Player player = profile.getPlayer();

                    if (player != null) {
                        player.kickPlayer(String.join("\n", punishment.getPunishmentMessage()));
                    }

                    Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

                    pigeon.broadcast(new PunishmentBroadcastPayload(punishment.getId(), profile.getUuid()));
                });
    }

}
