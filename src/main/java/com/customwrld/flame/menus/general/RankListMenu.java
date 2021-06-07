package com.customwrld.flame.menus.general;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.TextSplitter;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        contents.fill(ClickableItem.empty(Constants.filler));

        int i = 0;

        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
        sortedRanks.sort(Rank.COMPARATOR);

        for (Rank rank : sortedRanks) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.MENU_BAR);
            lore.add(CC.GRAY + "Weight: " + CC.WHITE + rank.getWeight());
            lore.add(CC.GRAY + "Prefix: " + CC.WHITE + rank.getPrefix());
            lore.add(CC.MENU_BAR);
            List<String> permissions = rank.getAllPermissions();

            lore.add(CC.GRAY + "Permissions [" + permissions.size() + "]");

            if (!permissions.isEmpty()) {
                lore.addAll(TextSplitter.split(46, StringUtils.join(permissions, " "), CC.WHITE, ", "));
            }

            List<Rank> inherited = rank.getInherited();

            lore.add(CC.MENU_BAR);
            lore.add(CC.GRAY + "Inherited Ranks [" + inherited.size() + "]");

            if (!rank.getInherited().isEmpty()) {
                List<String> rankNames = rank.getInherited()
                        .stream().sorted(Rank.COMPARATOR)
                        .map(inheritedRank -> ChatColor.WHITE + inheritedRank.getName())
                        .collect(Collectors.toList());

                lore.addAll(TextSplitter.split(46, StringUtils.join(rankNames, " "), "", ", "));
            }

            lore.add(CC.MENU_BAR);

            items[i] = ClickableItem.of(new ItemFactory(CC.toMaterial(rank.getColor()))
                    .setName(rank.getColor() + rank.getName())
                    .setLore(lore)
                    .build(), e -> {
            });
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.set(0, 0, ClickableItem.of(new ItemFactory(Material.ARROW)
                        .setName("&6Previous Page")
                        .build(),
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(new ItemFactory(Material.ARROW)
                        .setName("&6Next Page")
                        .build(),
                e -> inventory.open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
