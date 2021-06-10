package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Util;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
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

                    if (profile.getActivePunishmentByType(Punishment.Type.BLACKLIST) != null) {
                        sender.sendMessage(CC.RED + "That player already has an pre-existing punishment.");
                        return;
                    }

                    UUID addedBy = null;

                    if (sender instanceof Player) {
                        addedBy = (((Player) sender).getUniqueId());
                    }

                    Punishment punishment = new Punishment(
                            Util.generateId(),
                            profile.getUuid(),
                            Punishment.Type.BLACKLIST,
                            addedBy,
                            System.currentTimeMillis(),
                            reason,
                            -1L
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