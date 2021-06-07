package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.*;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureStage;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureType;
import com.customwrld.flame.rank.Rank;
import org.bukkit.Sound;
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
                .size(4, 9)
                .title(CC.WHITE + "Rank Selection")
                .build();
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];

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
                                GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedureType.GRANT, GrantProcedureStage.REQUIRE_TIME);
                                procedure.setGrant(new Grant(UUID.randomUUID(), rank, player.getUniqueId(), 0, "", 0));

                                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                                new GrantTimeMenu(player, profile, procedure);
                            }
                        });
                i++;
            }
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        contents.set(SlotPos.of(3, 2), ClickableItem.of(null, e -> {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            inventory.open(player, pagination.previous().getPage());
        }));

        contents.set(SlotPos.of(3, 6), ClickableItem.of(null, e -> {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            inventory.open(player, pagination.next().getPage());
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {}
}