package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FixCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("fix")
                .withPermission("flame.fix")
                .withOptionalArgument(new PlayerArgument("player"), () -> null)
                .executes((sender, args) -> {
                    Player player;

                    if (args.length == 0 || args[0] == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(CC.RED + "You must provide a player who's item you want to fix.");
                            return;
                        }
                        player = (Player) sender;
                    } else {
                        player = (Player) args[0];
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item.getItemMeta() == null || item.getType() == Material.AIR || !(item.getItemMeta() instanceof Damageable)) {
                        sender.sendMessage(CC.RED + "That item cannot be fixed.");
                        return;
                    }

                    Damageable meta = ((Damageable) item.getItemMeta());
                    meta.setDamage(0);
                    item.setItemMeta((ItemMeta) meta);
                    player.sendMessage(CC.GREEN + "The item in your hand has been fixed!");
                    if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                        sender.sendMessage(CC.GREEN + "You have fixed " + player.getName() + "'s hand!");
                    }
                });
    }

}
