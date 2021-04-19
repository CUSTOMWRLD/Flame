package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StackCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("stack")
                .withPermission("flame.stack")
                .withOptionalArgument(new IntegerArgument("amount", 1, 127), () -> 64)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    int amount = (Integer) args[0];

                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item.getItemMeta() == null || item.getType() == Material.AIR) {
                        player.sendMessage(CC.RED + "Could not add additional items to your hand.");
                        return;
                    }

                    player.sendMessage(CC.GREEN + "The item in your hands amount has been changed to " + amount + ".");
                    item.setAmount(amount);
                });
    }

}
