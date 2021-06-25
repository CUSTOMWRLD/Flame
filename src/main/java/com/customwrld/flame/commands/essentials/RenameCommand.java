package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.MC;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    Component itemName = MC.Style.fromLegacy((String) args[0]);

                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();

                    meta.displayName(itemName);
                    item.setItemMeta(meta);

                    player.sendMessage(Component.text()
                            .append(Component.text("Your item has been renamed to ").color(NamedTextColor.GREEN))
                            .append(itemName));
                });
    }

}
