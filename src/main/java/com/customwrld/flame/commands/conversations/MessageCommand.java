package com.customwrld.flame.commands.conversations;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.conversation.ConversationMessagePayload;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MessageCommand extends Command {

    public MessageCommand() {
        this.command = "message";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("message"))
                .withAliases("msg", "whisper", "tell", "t")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile profile = Profile.getProfile(((Player) sender).getUniqueId());
                    Profile target = (Profile) args[0];
                    String message = (String) args[1];

                    if (player.getUniqueId().equals(target.getId())) {
                        player.sendMessage(Component.text("You cannot message yourself!")
                                .color(MC.CC.RED.getTextColor()));
                        return;
                    }

                    if (target.api().isIgnoring(profile) || !target.getOptionsProfile().isReceivingConversations()) {
                        player.sendMessage(Component.text("That player is not receiving new conversations right now.")
                                .color(MC.CC.RED.getTextColor()));
                        return;
                    }

                    CustomLib.getCustomLib().getPigeon().broadcast(new ConversationMessagePayload(player.getUniqueId(), target.getId(), message));
                });
    }

}
