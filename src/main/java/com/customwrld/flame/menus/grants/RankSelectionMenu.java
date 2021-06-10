package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.*;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.rank.Rank;
import com.customwrld.flame.util.Util;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            if (!rank.isDefaultRank()) {
                List<String> lore = new ArrayList<>();
                lore.add(CC.MENU_BAR);
                lore.add("&7Click to grant " + rank.getColor() + rank.getName() + " &7to " + FlameAPI.getColoredName(profile));
                lore.add(CC.MENU_BAR);

                items[i] = ClickableItem.of(new ItemFactory(CC.toMaterial(rank.getColor()))
                                .setName(rank.getColor() + rank.getName())
                                .setLore(lore)
                                .build(),
                        e -> {
                            if (FlameAPI.canStartProcedure(player)) {
                                GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedure.Type.ADD, GrantProcedure.Stage.REQUIRE_TIME);
                                procedure.setGrant(new Grant(UUID.randomUUID(), rank, player.getUniqueId(), 0, "", 0));

                                new GrantDurationMenu(player, profile, procedure);
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