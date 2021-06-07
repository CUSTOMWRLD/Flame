//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.Flame;
//import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
//import com.customwrld.flame.pigeon.payloads.RecalculatePlayerPayload;
//import com.customwrld.flame.profile.Profile;
//import com.customwrld.flame.profile.punishment.Punishment;
//import com.customwrld.flame.profile.punishment.PunishmentType;
//import com.customwrld.flame.util.Arguments;
//import dev.jorel.commandapi.CommandAPICommand;
//import org.bukkit.command.ConsoleCommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.UUID;
//
//public class OpCommand extends Command {
//
//    public replace() {
//        this.command = "replace";
//        this.permission = "flame.replace";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return null;
//    }
//
//    public static CommandAPICommand command() {
//        return new CommandAPICommand("op")
//                .withPermission("flame.op")
//                .withSubcommand(new CommandAPICommand("add")
//                        .withArguments(Arguments.profileArgument("player"))
//                        .withPermission("flame.op.add")
//                        .executes((sender, args) -> {
//                            Profile targetProfile = (Profile) args[0];
//
//                            if (sender instanceof ConsoleCommandSender) {
//                                if (targetProfile.getStaffProfile().operator()) {
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is already opped!");
//                                } else {
//                                    targetProfile.getStaffProfile().operator(true);
//                                    targetProfile.save();
//                                    sender.sendMessage(CC.GREEN + targetProfile.getName() + " has been opped!");
//                                    new RecalculatePlayerPayload(targetProfile.getUuid()).send();
//                                }
//                                return;
//                            }
//
//                            Player player = (Player) sender;
//                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                            if (playerProfile == null || !playerProfile.isLoaded()) {
//                                sender.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format()); // TODO: Change this to proper message
//                                return;
//                            }
//
//                            boolean uuidCheck = Flame.getFlame().getElevatedStaff().contains(playerProfile.getUuid());
//
//                            if (!uuidCheck) {
//                                Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BLACKLIST, null, System.currentTimeMillis(), "Security Violation", Integer.MAX_VALUE);
//
//                                playerProfile.getPunishments().add(punishment);
//                                playerProfile.save();
//
//                                player.kickPlayer(String.join("\n", punishment.getKickMessage()));
//
//                                new PunishmentBroadcastPayload(punishment, playerProfile.getUuid(), true).send();
//                                punishment.sendEmbed(playerProfile);
//                            } else {
//                                if (targetProfile.getStaffProfile().operator()) {
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is already opped!");
//                                } else {
//                                    targetProfile.getStaffProfile().operator(true);
//                                    targetProfile.save();
//                                    sender.sendMessage(CC.GREEN + targetProfile.getName() + " has been opped!");
//                                    new RecalculatePlayerPayload(targetProfile.getUuid()).send();
//                                }
//                            }
//                        }))
//                .withSubcommand(new CommandAPICommand("remove")
//                        .withArguments(Arguments.profileArgument("player"))
//                        .withPermission("flame.op.remove")
//                        .executes((sender, args) -> {
//                            Profile targetProfile = (Profile) args[0];
//
//                            if (sender instanceof ConsoleCommandSender) {
//                                if (targetProfile.getStaffProfile().operator()) {
//                                    targetProfile.getStaffProfile().operator(false);
//                                    targetProfile.save();
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " has been deopped!");
//                                    new RecalculatePlayerPayload(targetProfile.getUuid()).send();
//                                } else {
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is not opped!");
//                                }
//                                return;
//                            }
//
//                            Player player = (Player) sender;
//                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                            if (playerProfile == null || !playerProfile.isLoaded()) {
//                                sender.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format());
//                                return;
//                            }
//
//                            boolean uuidCheck = Flame.getFlame().getElevatedStaff().contains(playerProfile.getUuid());
//
//                            if (!uuidCheck) {
//                                Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BLACKLIST, null, System.currentTimeMillis(), "Security Violation", Integer.MAX_VALUE);
//
//                                playerProfile.getPunishments().add(punishment);
//                                playerProfile.save();
//
//                                player.kickPlayer(String.join("\n", punishment.getKickMessage()));
//
//                                new PunishmentBroadcastPayload(punishment, playerProfile.getUuid(), true).send();
//                                punishment.sendEmbed(playerProfile);
//                            } else {
//                                if (targetProfile.getStaffProfile().operator()) {
//                                    targetProfile.getStaffProfile().operator(false);
//                                    targetProfile.save();
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " has been deopped!");
//                                    new RecalculatePlayerPayload(targetProfile.getUuid()).send();
//                                } else {
//                                    sender.sendMessage(CC.RED + targetProfile.getName() + " is not opped!");
//                                }
//                            }
//                        }));
//    }
//
//}
