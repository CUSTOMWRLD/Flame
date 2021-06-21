package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.Config;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StackCommand extends Command {

    public StackCommand() {
        this.command = "stack";
        this.permission = "flame.stack";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
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
                        player.sendMessage(Component.text("Could not add additional items to your hand.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    player.sendMessage(Component.text("The item in your hands amount has been changed to " + amount + ".")
                            .color(NamedTextColor.GREEN));

                    item.setAmount(amount);
                });
    }

}
