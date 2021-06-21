package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                            sender.sendMessage(Component.text("You must provide a player who you want to ping.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(Component.text(player.getName() + "'s ping is " + ((CraftPlayer) player).getHandle().ping + "ms")
                                .color(NamedTextColor.AQUA));
                    } else {
                        sender.sendMessage(Component.text("Your ping is " + ((CraftPlayer) player).getHandle().ping + "ms")
                                .color(NamedTextColor.AQUA));
                    }
                });
    }

}
