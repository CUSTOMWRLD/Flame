package com.customwrld.flame.commands.bungeecord;

import com.customwrld.flame.pigeon.payloads.BroadcastMessagePayload;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;

public class AlertCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("alert")
                .withPermission("flame.alert")
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    String message = (String) args[0];

                    new BroadcastMessagePayload(message).send();
                });
    }

}
