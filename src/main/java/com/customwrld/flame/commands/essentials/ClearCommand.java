package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ClearCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("clear")
                .withPermission("flame.clear")
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(CC.RED + "You must provide a player who's inventory you want to clear.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    player.getInventory().clear();
                    player.sendMessage(CC.RED + "Your inventory has been cleared");
                    if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.GREEN + "You cleared " + player.getName() + "'s inventory.");
                    }
                });
    }

}
