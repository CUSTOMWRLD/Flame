package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.modules.rank.RankAPI;
import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.FlameAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RankSelectionMenu implements InventoryProvider {

    private final Profile profile;
    private final SmartInventory inventory;

    public RankSelectionMenu(Player player, Profile profile) {
        this.profile = profile;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(5, 9)
                .title("Rank Selection")
                .build();
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];

        contents.fill(ClickableItem.empty(Util.FILLER));

        int i = 0;

        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
        sortedRanks.sort((rank1, rank2) -> rank2.getWeight() - rank1.getWeight());

        for (Rank rank : sortedRanks) {
            if (!rank.api().isDefaultRank()) {
                items[i] = ClickableItem.of(new ItemFactory(Material.LEATHER_CHESTPLATE)
                                .setLeatherColor(Color.fromRGB(rank.getColor().getRed(), rank.getColor().getGreen(), rank.getColor().getBlue()))
                                .setName(Component.text(rank.getName())
                                        .color(TextColor.color(rank.getColor().getRGB()))
                                        .decoration(TextDecoration.ITALIC, false))
                                .setLore(
                                        Minecraft.Style.MENU_SEPARATOR,
                                        Component.text()
                                                .append(Component.text("Click to grant ")
                                                        .color(NamedTextColor.GRAY))
                                                .append(Component.text(rank.getName())
                                                        .color(TextColor.color(rank.getColor().getRGB())))
                                                .append(Component.text(" to ")
                                                        .color(NamedTextColor.GRAY))
                                                .append(FlameAPI.getColoredName(this.profile))
                                                .build()
                                                .decoration(TextDecoration.ITALIC, false),
                                        Minecraft.Style.MENU_SEPARATOR)
                                .build(),
                        e -> {
                            if (FlameAPI.canStartProcedure(player)) {
                                GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedure.Type.ADD, GrantProcedure.Stage.REQUIRE_TIME);
                                procedure.setGrant(new Grant(profile.getId(), rank.getUuid(), player.getUniqueId(), 0, "", 0));

                                new GrantDurationMenu(player, procedure);
                            }
                        });
                i++;
            }
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        Util.addNavigation(player, this.inventory, contents);
    }

}