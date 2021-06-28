package com.customwrld.flame.commands.conversations;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.conversation.ConversationReplyPayload;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        this.command = "reply";
        this.aliases = new String[]{"r"};
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withAliases(this.aliases)
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    String message = (String) args[0];

                    CustomLib.getCustomLib().getPigeon().broadcast(new ConversationReplyPayload(player.getUniqueId(), message));
                });
    }

}
