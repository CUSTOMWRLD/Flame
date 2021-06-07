package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends Command {

    public FeedCommand() {
        this.command = "feed";
        this.permission = "flame.feed";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(CC.RED + "You must provide a player you want to feed.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    player.setFoodLevel(20);
                    player.sendMessage(CC.GREEN + "You have been fed!");
                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.GREEN + "You fed " + player.getName() + "!");
                    }
                });
    }

}
