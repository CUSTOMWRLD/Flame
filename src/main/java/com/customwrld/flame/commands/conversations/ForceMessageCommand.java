package com.customwrld.flame.commands.conversations;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.conversation.Conversation;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class ForceMessageCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("forcemessage")
                .withPermission("flame.forcemessage")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile targetProfile = (Profile) args[0];
                    String message = (String) args[1];

                    Player player = (Player) sender;
                    Player target = targetProfile.getPlayer();
                    Profile playerProfile = Profile.getByUuid(player.getUniqueId());

                    if (target == null) {
                        player.sendMessage(CC.RED + "A player with that name could not be found.");
                        return;
                    }

                    if (player == target) {
                        player.sendMessage(CC.RED + "You cannot message yourself!");
                        return;
                    }

                    Conversation conversation = playerProfile.getConversations().getOrCreateConversation(target);
                    conversation.sendMessage(player, target, message);
                });
    }

}
