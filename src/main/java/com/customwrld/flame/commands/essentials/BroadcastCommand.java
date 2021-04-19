package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;

public class BroadcastCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("broadcast")
                .withPermission("flame.broadcast")
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    String message = (String) args[0];

                    Bukkit.broadcastMessage(CC.translate(message));
                });
    }

}
