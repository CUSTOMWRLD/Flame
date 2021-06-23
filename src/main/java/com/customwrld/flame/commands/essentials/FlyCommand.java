package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends Command {

    public FlyCommand() {
        this.command = "replace";
        this.permission = "flame.replace";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand("fly")
                .withPermission("flame.fly")
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(Component.text("You must provide a player you want to give flight to.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                        player = (Player) sender;

                    } else {
                        player = (Player) args[0];
                    }

                    if (player.isFlying()) {
                        player.setFlying(false);
                        player.sendMessage(Component.text("Flight mode has been disabled!")
                                .color(NamedTextColor.RED));
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(Component.text("You disabled " + player.getName() + "'s fly!")
                                    .color(NamedTextColor.GREEN));
                        }
                    } else {
                        player.setFlying(true);
                        player.sendMessage(Component.text("Flight mode has been enabled!")
                                .color(NamedTextColor.GREEN));
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(Component.text("You enabled " + player.getName() + "'s fly!")
                                    .color(NamedTextColor.GREEN));
                        }
                    }
                });
    }

}
