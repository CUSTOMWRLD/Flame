package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class FlyCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("fly")
                .withPermission("flame.fly")
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(CC.RED + "You must provide a player who's item you want to make fly.");
                            return;
                        }
                        player = (Player) sender;

                    } else {
                        player = (Player) args[0];
                    }

                    if (player.isFlying()) {
                        player.setFlying(false);
                        player.sendMessage(CC.RED + "Fly mode has been disabled!");
                        if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(CC.GREEN + "You disabled " + player.getName() + "'s fly!");
                        }
                    } else {
                        player.setFlying(true);
                        player.sendMessage(CC.GREEN + "Fly mode has been enabled");
                        if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(CC.GREEN + "You enabled " + player.getName() + "'s fly!");
                        }
                    }
                });
    }

}
