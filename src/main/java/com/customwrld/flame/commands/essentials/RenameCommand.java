package com.customwrld.flame.commands.essentials;

import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RenameCommand extends Command {

    public RenameCommand() {
        this.command = "rename";
        this.permission = "flame.rename";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new GreedyStringArgument("name"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();
                    
                    if(item.getType().isAir() || item.getType().isEmpty()) {
                        player.sendMessage(Component.text("You must be holding an item to rename it.", MC.CC.RED.getTextColor()));
                        return;
                    }

                    Component itemName = MC.Style.fromLegacy((String) args[0]);

                    ItemFactory itemFactory = new ItemFactory(item).setName(itemName);

                    item.setItemMeta(itemFactory.build().getItemMeta());

                    player.sendMessage(Component.text()
                            .append(Component.text("Your item has been renamed to ", MC.CC.GREEN.getTextColor()))
                            .append(itemName));
                });
    }

}
