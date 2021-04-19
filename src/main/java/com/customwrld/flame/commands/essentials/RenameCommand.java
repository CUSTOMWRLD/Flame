package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("rename")
                .withPermission("flame.rename")
                .withArguments(new GreedyStringArgument("name"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    String name = (String) args[0];

                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(CC.translate(name));
                    item.setItemMeta(meta);
                    player.sendMessage(CC.GREEN + "Your item has been renamed!");
                });
    }

}
