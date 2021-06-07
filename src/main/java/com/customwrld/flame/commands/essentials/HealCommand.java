package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
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
                            sender.sendMessage(CC.RED + "You must provide a player you want to heal.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

                    if (maxHealth != null) {
                        player.setHealth(maxHealth.getValue());
                        player.sendMessage(CC.GREEN + "You have been healed!");
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(CC.GREEN + "You healed " + player.getName() + "!");
                        }
                    } else {
                        if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                            sender.sendMessage(CC.RED + "Server was unable to heal " + player.getName() + ".");
                        } else {
                            sender.sendMessage(CC.RED + "Server was unable to you.");
                        }
                        sender.sendMessage();
                    }
                });
    }

}
