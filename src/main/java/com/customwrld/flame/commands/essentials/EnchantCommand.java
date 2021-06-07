package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand extends Command {

    public EnchantCommand() {
        this.command = "enchant";
        this.permission = "flame.enchant";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new PlayerArgument("player"))
                .withArguments(new EnchantmentArgument("enchantment"))
                .withArguments(new IntegerArgument("level", 0, 32767))
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    Enchantment enchantment = (Enchantment) args[1];
                    int level = (Integer) args[2];

                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item.getItemMeta() == null || item.getType() == Material.AIR) {
                        player.sendMessage(CC.RED + "Could not add an enchantment to your held item.");
                        return;
                    }

                    if (level == 0) {
                        player.sendMessage(CC.RED + "A " + enchantment.getKey().getKey().replace("_", " ") + " enchantment has been removed from your held item.");
                        item.removeEnchantment(enchantment);
                    } else {
                        player.sendMessage(CC.GREEN + "A " + enchantment.getKey().getKey().replace("_", " ") + " enchantment with level " + level + " has been applied to your held item.");
                        item.addUnsafeEnchantment(enchantment, level);
                    }
                });
    }

}
