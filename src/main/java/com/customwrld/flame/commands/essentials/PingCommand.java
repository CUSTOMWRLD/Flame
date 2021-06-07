package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends Command {

    public PingCommand() {
        this.command = "ping";
        this.permission = "flame.ping";
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
                            sender.sendMessage(CC.RED + "You must provide a player who you want to ping.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.AQUA + player.getName() + "'s ping is " + CC.BLUE + ((CraftPlayer) player).getHandle().ping + "ms");
                    } else {
                        sender.sendMessage(CC.AQUA + "Your ping is " + CC.BLUE + ((CraftPlayer) player).getHandle().ping + "ms");
                    }
                });
    }

}
