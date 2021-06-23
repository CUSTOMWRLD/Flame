//package com.customwrld.flame.commands.player;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.FlameAPI;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import org.apache.commons.lang.StringUtils;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.UUID;
//
//public class IgnoreCommand extends Command {
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
//        return new CommandAPICommand("ignore")
//                .withSubcommand(new CommandAPICommand("add")
//                        .withArguments(Arguments.profileArgument("player"))
//                        .executes((sender, args) -> {
//                            if (!(sender instanceof Player)) {
//                                sender.sendMessage(Config.PLAYER_ONLY.format());
//                                return;
//                            }
//
//                            Player player = (Player) sender;
//                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                            if (playerProfile == null || !playerProfile.isLoaded()) {
//                                player.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format());
//                                return;
//                            }
//
//                            Profile targetProfile = (Profile) args[0];
//                            Player target = targetProfile.getPlayer();
//
//                            if (target == null) {
//                                player.sendMessage(CC.RED + "A player with that name could not be found.");
//                                return;
//                            }
//
//                            if (player == target) {
//                                player.sendMessage(CC.RED + "You cannot ignore yourself!");
//                                return;
//                            }
//
//                            if (playerProfile.isIgnoring(targetProfile)) {
//                                player.sendMessage(CC.RED + "You are already ignoring this player!");
//                                return;
//                            }
//
//                            playerProfile.getIgnored().add(targetProfile.getUuid());
//                            playerProfile.save();
//
//                            player.sendMessage(CC.GREEN + "You have added " + targetProfile.getName() + " to your ignore list!");
//                        })
//                ).withSubcommand(new CommandAPICommand("remove")
//                        .withArguments(Arguments.profileArgument("player"))
//                        .executes((sender, args) -> {
//                            if (!(sender instanceof Player)) {
//                                sender.sendMessage(Config.PLAYER_ONLY.format());
//                                return;
//                            }
//
//                            Player player = (Player) sender;
//                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                            if (playerProfile == null || !playerProfile.isLoaded()) {
//                                player.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format());
//                                return;
//                            }
//
//                            Profile targetProfile = (Profile) args[0];
//                            Player target = targetProfile.getPlayer();
//
//                            if (target == null) {
//                                player.sendMessage(CC.RED + "A player with that name could not be found.");
//                                return;
//                            }
//
//                            if (player == target) {
//                                player.sendMessage(CC.RED + "You cannot unignore yourself!");
//                                return;
//                            }
//
//                            if (!playerProfile.isIgnoring(targetProfile)) {
//                                player.sendMessage(CC.RED + "You aren't ignoring this player!");
//                                return;
//                            }
//
//                            playerProfile.getIgnored().remove(targetProfile);
//                            playerProfile.save();
//
//                            player.sendMessage(CC.RED + "You have removed " + targetProfile.getName() + " from your ignore list!");
//                        })
//                ).withSubcommand(new CommandAPICommand("list")
//                        .executes((sender, args) -> {
//                            if (!(sender instanceof Player)) {
//                                sender.sendMessage(Config.PLAYER_ONLY.format());
//                                return;
//                            }
//
//                            Player player = (Player) sender;
//                            Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                            if (playerProfile == null || !playerProfile.isLoaded()) {
//                                player.sendMessage(CC.RED + Config.FAILED_TO_LOAD_PROFILE.format());
//                                return;
//                            }
//
//                            List<UUID> ignoredPlayers = playerProfile.getIgnored();
//
//                            if (ignoredPlayers.size() <= 0) {
//                                player.sendMessage(CC.RED + "You aren't ignoring anyone.");
//                                return;
//                            }
//
////                            ignoredPlayers.sort(Comparator.comparing(Profile::getName));
//
//                            List<String> playerNames = new ArrayList<>();
//
////                            for (Profile profile : ignoredPlayers) {
////                                playerNames.add(FlameAPI.getColoredName(profile));
////                            }
//
//                            player.sendMessage("(" + playerProfile.getIgnored().size() + "): " +
//                                    StringUtils.join(playerNames, CC.WHITE + ", "));
//                        })
//                );
//    }
//
//}
