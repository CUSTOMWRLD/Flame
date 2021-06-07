package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends Command {

    public KillCommand() {
        this.command = "kill";
        this.permission = "flame.kill";
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
                            sender.sendMessage(CC.RED + "You must provide a player who you want to kill.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    player.setHealth(0);
                    player.sendMessage(CC.RED + "You were killed by magic.");
                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.GREEN + "You killed " + player.getName());
                    }
                });
    }

}
