//package com.customwrld.flame.commands.conversations;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.profile.Profile;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.GreedyStringArgument;
//import org.bukkit.entity.Player;
//
//public class MessageCommand extends Command {
//
//    public static CommandAPICommand command() {
//        return new CommandAPICommand("message")
//                .withArguments(Arguments.profileArgument("player"))
//                .withArguments(new GreedyStringArgument("message"))
//                .withAliases("msg", "whisper", "tell", "t")
//                .executes((sender, args) -> {
//                    if (!(sender instanceof Player)) {
//                        sender.sendMessage(Config.PLAYER_ONLY.format());
//                        return;
//                    }
//
//                    Profile targetProfile = (Profile) args[0];
//                    String message = (String) args[1];
//
//                    Player player = (Player) sender;
//                    Player target = targetProfile.getPlayer();
//                    Profile playerProfile = Profile.getByUuid(player.getUniqueId());
//
//                    if (target == null) {
//                        player.sendMessage(CC.RED + "A player with that name could not be found.");
//                        return;
//                    }
//
//                    if (player == target) {
//                        player.sendMessage(CC.RED + "You cannot message yourself!");
//                        return;
//                    }
//
//                    if (targetProfile.getConversations().canBeMessagedBy(player)) {
//                        Conversation conversation = playerProfile.getConversations().getOrCreateConversation(target);
//
//                        if (conversation.validate()) {
//                            conversation.sendMessage(player, target, message);
//                        } else {
//                            player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
//                        }
//                    } else {
//                        player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
//                    }
//                });
//    }
//
//}
