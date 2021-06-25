package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends Command {

    public HealCommand() {
        this.command = "heal";
        this.permission = "flame.heal";
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
                            sender.sendMessage(Component.text("You must provide a player you want to heal.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

                    if (maxHealth != null) {
                        player.setHealth(maxHealth.getValue());
                        player.sendMessage(Component.text("You have been healed!")
                                .color(NamedTextColor.GREEN));
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(Component.text("You healed " + player.getName() + "!")
                                    .color(NamedTextColor.GREEN));
                        }
                    } else {
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(Component.text("Server was unable to heal " + player.getName() + ".")
                                    .color(NamedTextColor.RED));
                        } else {
                            sender.sendMessage(Component.text("Server was unable to heal you.")
                                    .color(NamedTextColor.RED));
                        }
                    }
                });
    }

}
