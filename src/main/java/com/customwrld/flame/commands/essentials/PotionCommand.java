package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.PotionUtil;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.PotionEnum;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PotionEffectArgument;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionCommand extends Command {

    public PotionCommand() {
        this.command = "potion";
        this.permission = "flame.potion";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new PotionEffectArgument("effect"))
                .withArguments(new IntegerArgument("duration", 1, 10000000))
                .withArguments(new IntegerArgument("potency", 1, 128))
                .withArguments(Arguments.potionTypeArgument("type"))
                .withOptionalArgument(Arguments.hexArgument("color"), () -> "null")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    PotionEffectType effect = (PotionEffectType) args[0];
                    Integer duration = (Integer) args[1];
                    Integer potency = (Integer) args[2];
                    PotionEnum type = (PotionEnum) args[3];

                    String hex;
                    if (args.length == 4 || args[4] == null) {
                        hex = "null";
                    } else {
                        hex = (String) args[4];
                    }

                    Color color = effect.getColor();

                    if (!hex.equals("null")) {
                        java.awt.Color hexColor = CC.hexToRGB(hex);
                        color = Color.fromRGB(hexColor.getRed(), hexColor.getGreen(), hexColor.getBlue());
                    }

                    String potionDisplay = PotionUtil.valueOf(effect.getName().toUpperCase()).getDisplay();

                    ItemStack item = new ItemStack(type.getMaterial());
                    PotionMeta meta = ((PotionMeta) item.getItemMeta());
                    meta.setColor(color);
                    meta.addCustomEffect(new PotionEffect(effect, type == PotionEnum.LINGERING ? duration * 80 : duration * 20, potency - 1), true);
                    meta.setDisplayName(CC.WHITE + type.getName() + " " + potionDisplay);
                    item.setItemMeta(meta);
                    player.getInventory().addItem(item);
                    player.sendMessage(CC.GREEN + "A " + potionDisplay + " Potion has been added to your inventory!");
                });
    }

}
