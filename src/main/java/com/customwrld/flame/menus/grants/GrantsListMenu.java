package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.grant.GrantAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        this.inventory = SmartInventory
                .builder()
                .provider(this)
                .size(5, 9)
                .title(profile.getName() + "'s Grants (" + profile.getGrants().size() + ")")
                .build();
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[profile.getGrants().size()];

        contents.fill(ClickableItem.empty(Util.FILLER));

        int i = 0;

        List<Grant> sortedGrants = new ArrayList<>();
        List<Grant> activeGrants = new ArrayList<>();
        List<Grant> removedGrants = new ArrayList<>();

        for (Grant grant : this.profile.getCachedGrants()) {
            if (grant.isRemoved()) {
                removedGrants.add(grant);
            } else {
                activeGrants.add(grant);
            }
        }

        activeGrants.sort(GrantAPI.COMPARATOR);
        removedGrants.sort(GrantAPI.COMPARATOR);

        sortedGrants.addAll(activeGrants);
        sortedGrants.addAll(removedGrants);

        for (Grant grant : sortedGrants) {
            String addedBy = "Console";

            if (grant.getAddedByUUID() != null) {
                addedBy = "Could not fetch...";

                Profile addedByProfile = Profile.getProfile(null, grant.getAddedByUUID());

                if (addedByProfile != null) {
                    addedBy = addedByProfile.getName();
                }
            }

            String removedBy = "Console";

            if (grant.getRemovedByUUID() != null) {
                removedBy = "Could not fetch...";

                Profile removedByProfile = Profile.getProfile(null, grant.getRemovedByUUID());

                if (removedByProfile != null) {
                    removedBy = removedByProfile.getName();
                }
            }

            List<Component> lore = new ArrayList<>();

            lore.add(MC.Style.MENU_SEPARATOR);
            lore.add(Component.text().append(Component.text("Rank: ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(grant.api().getRank().getName())
                            .color(NamedTextColor.DARK_AQUA))
                    .build()
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text().append(Component.text("Added On: ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(TimeUtil.dateToString(new Date(grant.getAddedAt()), true))
                            .color(NamedTextColor.DARK_AQUA))
                    .build()
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text().append(Component.text("Added By: ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(addedBy)
                            .color(NamedTextColor.DARK_AQUA))
                    .build()
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text().append(Component.text("Added Reason: ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(grant.getAddedReason())
                            .color(NamedTextColor.DARK_AQUA))
                    .build()
                    .decoration(TextDecoration.ITALIC, false));

            if (!grant.api().isPermanent() && grant.getDuration() != -1L) {
                if (!grant.api().isActive()) {
                    lore.add(Component.text().append(Component.text("Remaining: ")
                            .color(NamedTextColor.GRAY))
                            .append(Component.text(grant.api().getTimeRemaining())
                                    .color(NamedTextColor.DARK_AQUA))
                            .build()
                            .decoration(TextDecoration.ITALIC, false));
                }
            }

            lore.add(Component.text().append(Component.text("Duration: ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(grant.api().getDurationText())
                            .color(NamedTextColor.DARK_AQUA))
                    .build()
                    .decoration(TextDecoration.ITALIC, false));

            if (grant.isRemoved()) {
                lore.add(MC.Style.MENU_SEPARATOR);
                lore.add(Component.text().append(Component.text("Removed On: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(TimeUtil.dateToString(new Date(grant.getRemovedAt()), true))
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text().append(Component.text("Removed By: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(removedBy)
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text().append(Component.text("Removed Reason: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(grant.getRemovedReason())
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));
            } else {
                if (!grant.api().getRank().api().isDefaultRank() && !grant.api().hasExpired() && FlameAPI.canStartProcedure(player)) {
                    if (player.hasPermission("flame.rank.remove")) {
                        lore.add(MC.Style.MENU_SEPARATOR);
                        lore.add(Component.text().append(Component.text("Click to remove this grant.")
                                .color(NamedTextColor.DARK_AQUA))
                                .build()
                                .decoration(TextDecoration.ITALIC, false));
                    }
                }
            }
            lore.add(MC.Style.MENU_SEPARATOR);

            Material material = Material.BOOK;

            if (!grant.isRemoved() && !grant.api().hasExpired()) {
                material = Material.ENCHANTED_BOOK;
            }

            items[i] = ClickableItem.of(new ItemFactory(material)
                            .setName(Component.text(grant.api().getRank().getName())
                                    .color(TextColor.color(grant.api().getRank().getColor().getRGB()))
                                    .decoration(TextDecoration.ITALIC, false))
                            .setLore(lore)
                            .build(),
                    e -> {
                        if (!grant.isRemoved() && !grant.api().hasExpired() && !grant.api().getRank().api().isDefaultRank() && FlameAPI.canStartProcedure(player)) {
                            if (player.hasPermission("flame.grants.remove")) {
                                GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedure.Type.REMOVE, GrantProcedure.Stage.REQUIRE_REASON);
                                procedure.setGrant(grant);

                                player.sendMessage(Component.text("Type a reason for removing this grant in chat...")
                                        .color(MC.CC.GREEN.getTextColor()));
                                player.closeInventory();
                            }
                        }
                    });
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        Util.addNavigation(player, this.inventory, contents);
    }

}