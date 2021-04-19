package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class KillCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("kill")
                .withPermission("flame.kill")
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(CC.RED + "You must provide a player who you want to kill.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    player.setHealth(0);
                    player.sendMessage(CC.RED + "You were killed by magic.");
                    if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.GREEN + "You killed " + player.getName());
                    }
                });
    }

}
