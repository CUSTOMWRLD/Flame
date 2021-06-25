package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                            sender.sendMessage(Component.text("You must provide a player who you want to kill.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    player.setHealth(0);
                    player.sendMessage(Component.text("You were killed by magic.")
                            .color(NamedTextColor.RED));
                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(Component.text("You killed " + player.getName())
                                .color(NamedTextColor.GREEN));
                    }
                });
    }

}
