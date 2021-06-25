package com.customwrld.flame.menus.general;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankListMenu implements InventoryProvider {

    private final SmartInventory inventory;

    public RankListMenu(Player player) {
        inventory = SmartInventory.builder()
                .provider(this)
                .size(5, 9)
                .title("Ranks")
                .build();
        inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];

        contents.fill(ClickableItem.empty(Util.FILLER));

        int i = 0;

        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
        sortedRanks.sort(Rank.COMPARATOR);

        for (Rank rank : sortedRanks) {
            List<Component> lore = new ArrayList<>();
            lore.add(MC.Style.MENU_SEPARATOR);
            lore.add(Component.text()
                    .append(Component.text("Weight: ")
                            .color(NamedTextColor.GRAY))
                    .append(Component.text(rank.getWeight())
                            .color(NamedTextColor.DARK_AQUA))
                    .build());
            lore.add(Component.text()
                    .append(Component.text("Prefix: ")
                            .color(NamedTextColor.GRAY))
                    .append(MC.Style.fromLegacy(rank.getPrefix()))
                    .build());
//            lore.add(MC.Style.MENU_SEPARATOR);
//            List<String> permissions = rank.api().getAllPermissions();

//            lore.add(Component.text("Permissions [" + permissions.size() + "]")
//                    .color(NamedTextColor.GRAY));
//
//            if (!permissions.isEmpty()) {
//                lore.addAll(TextSplitter.split(46, StringUtils.join(permissions, " "), CC.WHITE, ", "));
//            }
//
//            List<Rank> inherited = rank.getInherited();
//
//            lore.add(CC.MENU_BAR);
//            lore.add(CC.GRAY + "Inherited Ranks [" + inherited.size() + "]");
//
//            if (!rank.getInherited().isEmpty()) {
//                List<String> rankNames = rank.getInherited()
//                        .stream().sorted(Rank.COMPARATOR)
//                        .map(inheritedRank -> ChatColor.WHITE + inheritedRank.getName())
//                        .collect(Collectors.toList());
//
//                lore.addAll(TextSplitter.split(46, StringUtils.join(rankNames, " "), "", ", "));
//            }

            lore.add(MC.Style.MENU_SEPARATOR);
            lore.add(Component.text("Click to view rank permissions.")
                    .color(NamedTextColor.DARK_AQUA));
            lore.add(MC.Style.MENU_SEPARATOR);

            items[i] = ClickableItem.of(new ItemFactory(Material.LEATHER_CHESTPLATE)
                    .setLeatherColor(Color.fromRGB(rank.getColor().getRed(), rank.getColor().getGreen(), rank.getColor().getBlue()))
                    .setName(Component.text(rank.getName())
                            .color(TextColor.color(rank.getColor().getRGB()))
                            .decoration(TextDecoration.ITALIC, false))
                    .setName(Component.text(rank.getName())
                            .color(TextColor.color(rank.getColor().getRGB())))
                    .setLore(lore)
                    .build(), e -> {
            });
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        Util.addNavigation(player, this.inventory, contents);
    }

}
