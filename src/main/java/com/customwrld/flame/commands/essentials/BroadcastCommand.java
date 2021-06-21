package com.customwrld.flame.commands.essentials;

import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;

public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        this.command = "broadcast";
        this.permission = "flame.broadcast";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    String message = (String) args[0];

                    Bukkit.broadcast(Minecraft.Style.fromLegacy(message));
                });
    }
}
