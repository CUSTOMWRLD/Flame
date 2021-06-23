// TODO: Fix this command
//package com.customwrld.flame.commands.punishments;
//
//import com.customwrld.customlib.CustomLib;
//import com.customwrld.flame.pigeon.payloads.HandlePunishmentPayload;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.profile.punishment.Punishment;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import com.customwrld.flame.util.Util;
//import com.customwrld.pigeon.Pigeon;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.GreedyStringArgument;
//import org.bukkit.entity.Player;
//
//import java.util.UUID;
//
//public class WarnCommand extends Command {
//
//    public WarnCommand() {
//        this.command = "warn";
//        this.permission = "flame.warn";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withArguments(Arguments.profileArgument("player"))
//                .withArguments(new GreedyStringArgument("reason"))
//                .executes((sender, args) -> {
//                    Profile profile = (Profile) args[0];
//                    String reason = (String) args[1];
//
//                    UUID addedBy = null;
//
//                    if (sender instanceof Player) {
//                        Player executor = (Player) sender;
//
//                        if (Util.isElevatedStaff(executor, profile.getUuid())) {
//                            return;
//                        }
//
//                        addedBy = executor.getUniqueId();
//                    }
//
//                    Punishment punishment = new Punishment(
//                            Util.generateId(),
//                            profile.getUuid(),
//                            Punishment.Type.WARN,
//                            addedBy,
//                            System.currentTimeMillis(),
//                            reason,
//                            -1L
//                    );
//
//                    punishment.save();
//
//                    profile.getCachedPunishments().add(punishment);
//                    profile.getPunishments().add(punishment.getId());
//
//                    profile.save();
//
//                    Player player = profile.getPlayer();
//
//                    if (player != null) {
//                        player.kickPlayer(String.join("\n", punishment.getPunishmentMessage()));
//                    }
//
//                    Pigeon pigeon = CustomLib.getCustomLib().getPigeon();
//
//                    pigeon.broadcast(new HandlePunishmentPayload(punishment.getId(), profile.getUuid()));
//                });
//    }
//
//}
