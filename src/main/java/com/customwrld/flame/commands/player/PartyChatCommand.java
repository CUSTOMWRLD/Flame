package com.customwrld.flame.commands.player;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.party.PartyChatPayload;
import com.customwrld.flame.util.Command;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class PartyChatCommand extends Command {

    public PartyChatCommand() {
        this.command = "partychat";
        this.aliases = new String[]{"pchat"};
    }

    @Override
    public CommandAPICommand command() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand(this.command)
                .withAliases(this.aliases)
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    String message = (String) args[0];

                    pigeon.broadcast(new PartyChatPayload(player.getUniqueId(), message));
                });
    }

}
