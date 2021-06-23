package com.customwrld.flame.util;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.Flame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Util {

    public static final ItemStack NEXT_PAGE = new ItemFactory(Material.GRAY_CARPET)
            .setName(Component.text().append(Component.text("Next Page ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(">")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.BOLD, true))
                    .build()
                    .decoration(TextDecoration.ITALIC, false))
            .build();

    public static final ItemStack PREVIOUS_PAGE = new ItemFactory(Material.GRAY_CARPET)
            .setName(Component.text().append(Component.text("< ")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("Previous Page")
                            .color(NamedTextColor.GRAY))
                    .build()
                    .decoration(TextDecoration.ITALIC, false))
            .build();

    public static final ItemStack FILLER = new ItemFactory(Material.GRAY_STAINED_GLASS_PANE)
            .setName(Component.empty()).build();

    public static boolean isElevatedStaff(Player initiator, UUID player) {
        if (!Flame.getFlame().getElevatedStaff().contains(player)) {
            return false;
        } else {
            initiator.sendMessage(CC.RED + "You cannot use that command on that player.");
            return true;
        }
    }

    public static void addNavigation(Player player, SmartInventory inventory, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.set(0, 0, ClickableItem.of(PREVIOUS_PAGE,
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(NEXT_PAGE,
                e -> inventory.open(player, pagination.next().getPage())));
    }

}
