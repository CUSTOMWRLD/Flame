package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureStage;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GrantsListMenu implements InventoryProvider {

    private final Profile profile;
    private final SmartInventory inventory;

    public GrantsListMenu(Player player, Profile profile) {
        this.profile = profile;
        this.inventory = SmartInventory.builder()
                .id("grantsListMenu")
                .provider(this)
                .size(5, 9)
                .title(profile.getName() + "'s Grants (" + profile.getGrants().size() + ")")
                .build();
        this.inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[profile.getGrants().size()];

        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .title(" ")
                .build()));

        int i = 0;

        List<Grant> sortedGrants = new ArrayList<>();
        List<Grant> activeGrants = new ArrayList<>();
        List<Grant> removedGrants = new ArrayList<>();

        for (Grant grant : profile.getGrants()) {
            if (grant.isRemoved()) {
                removedGrants.add(grant);
            } else {
                activeGrants.add(grant);
            }
        }

        activeGrants.sort((rank1, rank2) -> rank2.getRank().getWeight() - rank1.getRank().getWeight());
        removedGrants.sort((rank1, rank2) -> rank2.getRank().getWeight() - rank1.getRank().getWeight());

        sortedGrants.addAll(activeGrants);
        sortedGrants.addAll(removedGrants);

        for (Grant grant : sortedGrants) {
            String addedBy = "Console";

            if (grant.getAddedBy() != null) {
                addedBy = "Could not fetch...";

                Profile addedByProfile = Profile.getByUuid(grant.getAddedBy());

                if (addedByProfile != null && addedByProfile.isLoaded()) {
                    addedBy = addedByProfile.getName();
                }
            }

            String removedBy = "Console";

            if (grant.getRemovedBy() != null) {
                removedBy = "Could not fetch...";

                Profile removedByProfile = Profile.getByUuid(grant.getRemovedBy());

                if (removedByProfile != null && removedByProfile.isLoaded()) {
                    removedBy = removedByProfile.getName();
                }
            }

            List<String> lore = new ArrayList<>();

            lore.add(CC.MENU_BAR);
            lore.add("&eRank: &c" + grant.getRank().getName());
            lore.add("&eAdded On: &c" + TimeUtil.dateToString(new Date(grant.getAddedAt())));
            lore.add("&eAdded By: &c" + addedBy);
            lore.add("&eAdded Reason: &c" + grant.getAddedReason());
            lore.add("&eDuration: &c" + grant.getDurationText());

            if (grant.isRemoved()) {
                lore.add(CC.MENU_BAR);
                lore.add("&eRemoved On: &c" + TimeUtil.dateToString(new Date(grant.getRemovedAt())));
                lore.add("&eRemoved By: &c" + removedBy);
                lore.add("&eRemoved Reason: &c" + grant.getRemovedReason());
            } else {
                if (!grant.getRank().isDefaultRank() && !grant.hasExpired() && FlameAPI.canStartProcedure(player)) {
                    lore.add(CC.MENU_BAR);

                    if (player.hasPermission("flame.rank.remove")) {
                        lore.add("&6Click to remove this grant.");
                    }
                }
            }

            lore.add(CC.MENU_BAR);

            Material material = Material.BOOK;

            if (!grant.isRemoved() && !grant.hasExpired()) {
                material = Material.ENCHANTED_BOOK;
            }

            items[i] = ClickableItem.of(new ItemBuilder()
                            .material(material)
                            .title(grant.getRank().getColor() + grant.getRank().getName())
                            .lores(lore)
                            .build(),
                    e -> {
                        if (!grant.isRemoved() && !grant.hasExpired() && !grant.getRank().isDefaultRank() && FlameAPI.canStartProcedure(player)) {
                            if (player.hasPermission("flame.grants.remove")) {
                                GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedureType.REMOVE, GrantProcedureStage.REQUIRE_REASON);
                                procedure.setGrant(grant);

                                player.sendMessage(CC.GREEN + "Type a reason for removing this grant in chat...");
                                player.closeInventory();
                            }
                        }
                    });
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(Material.ARROW)
                        .title("&6Previous Page")
                        .build(),
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.ARROW)
                        .title("&6Next Page")
                        .build(),
                e -> inventory.open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}