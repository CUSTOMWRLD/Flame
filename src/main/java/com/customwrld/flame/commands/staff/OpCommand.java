package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.pigeon.payloads.RecalculateProfilePayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.Punishment.Type;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Util;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OpCommand extends Command {

    public OpCommand() {
        this.command = "op";
        this.permission = "flame.op";
    }

    @Override
    public CommandAPICommand command() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(Arguments.profileArgument("player"))
                        .executes((sender, args) -> {
                            Profile targetProfile = (Profile) args[0];

                            if (sender instanceof ConsoleCommandSender) {
                                if (targetProfile.getStaffProfile().isOperator()) {
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is already opped!");
                                } else {
                                    targetProfile.getStaffProfile().setOperator(true);
                                    targetProfile.save();
                                    sender.sendMessage(CC.GREEN + targetProfile.getName() + " has been opped!");
                                    pigeon.broadcast(new RecalculateProfilePayload(targetProfile.getUuid()));
                                }
                                return;
                            }

                            Player player = (Player) sender;
                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());

                            if (playerProfile == null || !playerProfile.isLoaded()) {
                                sender.sendMessage(CC.RED + Config.COULD_NOT_RESOLVE_PLAYER.format());
                                return;
                            }

                            boolean uuidCheck = Flame.getFlame().getElevatedStaff().contains(playerProfile.getUuid());

                            if (!uuidCheck) {
                                Punishment punishment = new Punishment(
                                        Util.generateId(),
                                        UUID.randomUUID(),
                                        Type.BLACKLIST,
                                        null,
                                        System.currentTimeMillis(),
                                        "Security Violation",
                                        (long) Integer.MAX_VALUE
                                );

                                punishment.save();

                                playerProfile.getPunishments().add(punishment.getId());
                                playerProfile.save();

                                player.kickPlayer(String.join("\n", punishment.getPunishmentMessage()));

                                pigeon.broadcast(new PunishmentBroadcastPayload(punishment.getId(), playerProfile.getUuid()));
                            } else {
                                if (targetProfile.getStaffProfile().isOperator()) {
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is already opped!");
                                } else {
                                    targetProfile.getStaffProfile().setOperator(true);
                                    targetProfile.save();
                                    sender.sendMessage(CC.GREEN + targetProfile.getName() + " has been opped!");
                                    pigeon.broadcast(new RecalculateProfilePayload(targetProfile.getUuid()));
                                }
                            }
                        }))
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(Arguments.profileArgument("player"))
                        .withPermission("flame.op.remove")
                        .executes((sender, args) -> {
                            Profile targetProfile = (Profile) args[0];

                            if (sender instanceof ConsoleCommandSender) {
                                if (targetProfile.getStaffProfile().isOperator()) {
                                    targetProfile.getStaffProfile().setOperator(false);
                                    targetProfile.save();
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " has been deopped!");
                                    pigeon.broadcast(new RecalculateProfilePayload(targetProfile.getUuid()));
                                } else {
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is not opped!");
                                }
                                return;
                            }

                            Player player = (Player) sender;
                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());

                            if (playerProfile == null || !playerProfile.isLoaded()) {
                                sender.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format());
                                return;
                            }

                            boolean uuidCheck = Flame.getFlame().getElevatedStaff().contains(playerProfile.getUuid());

                            if (!uuidCheck) {
                                Punishment punishment = new Punishment(
                                        Util.generateId(),
                                        UUID.randomUUID(),
                                        Type.BLACKLIST,
                                        null,
                                        System.currentTimeMillis(),
                                        "Security Violation",
                                        (long) Integer.MAX_VALUE
                                );

                                punishment.save();

                                playerProfile.getPunishments().add(punishment.getId());
                                playerProfile.save();

                                player.kickPlayer(String.join("\n", punishment.getPunishmentMessage()));

                                pigeon.broadcast(new PunishmentBroadcastPayload(punishment.getId(), playerProfile.getUuid()));
                            } else {
                                if (targetProfile.getStaffProfile().isOperator()) {
                                    targetProfile.getStaffProfile().setOperator(false);
                                    targetProfile.save();
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " has been deopped!");
                                    pigeon.broadcast(new RecalculateProfilePayload(targetProfile.getUuid()));
                                } else {
                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is not opped!");
                                }
                            }
                        }));
    }

}
