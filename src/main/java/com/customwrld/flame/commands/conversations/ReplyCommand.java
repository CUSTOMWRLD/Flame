package com.customwrld.flame.commands.conversations;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.conversation.Conversation;
import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReplyCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("reply")
                .withArguments(new GreedyStringArgument("message"))
                .withAliases("r")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    String message = (String) args[0];

                    Profile playerProfile = Profile.getByUuid(player.getUniqueId());
                    Conversation conversation = playerProfile.getConversations().getLastRepliedConversation();

                    if (conversation != null) {
                        Profile targetProfile = Profile.getByUuid(conversation.getTarget());
                        Player target = Bukkit.getPlayer(conversation.getPartner(player.getUniqueId()));
                        if (conversation.validate() && targetProfile.getConversations().canBeMessagedBy(target)) {
                            if (target == null || !target.isOnline()) {
                                player.sendMessage(CC.RED + "That player is offline.");
                                return;
                            }
                            conversation.sendMessage(player, target, message);
                        } else {
                            player.sendMessage(CC.RED + "You can no longer reply to that player.");
                        }
                    } else {
                        player.sendMessage(CC.RED + "You have nobody to reply to.");
                    }
                });
    }

}
