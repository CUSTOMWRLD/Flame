package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FixCommand extends Command {

    public FixCommand() {
        this.command = "fix";
        this.permission = "flame.fix";
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
                            sender.sendMessage(Component.text("You must provide a player who's item you want to fix.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item.getItemMeta() == null || item.getType() == Material.AIR || !(item.getItemMeta() instanceof Damageable)) {
                        sender.sendMessage(Component.text("That item cannot be fixed.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    Damageable meta = ((Damageable) item.getItemMeta());
                    meta.setDamage(0);
                    item.setItemMeta((ItemMeta) meta);
                    player.sendMessage(Component.text("The item in your hand has been fixed!")
                            .color(NamedTextColor.GREEN));
                    if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(Component.text("You have fixed " + player.getName() + "'s hand!")
                                .color(NamedTextColor.GREEN));
                    }
                });
    }
}
