package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.grant.GrantAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

        ClickableItem[] items = new ClickableItem[profile.getGrants().size() + 1]; //Add space for the default grant

        contents.fill(ClickableItem.empty(Util.FILLER));

        List<Grant> sortedGrants = new ArrayList<>();
        List<Grant> activeGrants = new ArrayList<>();
        List<Grant> removedGrants = new ArrayList<>();

        activeGrants.add(Grant.DEFAULT_GRANT);

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

        Bukkit.getScheduler().runTaskLaterAsynchronously(Flame.getFlame(), () -> {
            int i = 0;
            for(Grant grant : sortedGrants) {
                try {
                    boolean isDefault = grant == Grant.DEFAULT_GRANT;

                    TextColor textColor = !isDefault ? TextColor.color(grant.api().getRank().api().getRankColor().getRGB()) :
                            MC.CC.WHITE.getTextColor();

                    String addedBy = "Console";

                    if(grant.getAddedByUUID() != null) {
                        addedBy = "Could not fetch...";

                        Profile addedByProfile = ProfileUtil.getProfile(grant.getAddedByUUID()).get(10, TimeUnit.SECONDS);
                        if(addedByProfile != null)
                            addedBy = addedByProfile.getName();
                    }

                    String removedBy = "Console";

                    if(grant.getRemovedByUUID() != null) {
                        removedBy = "Could not fetch...";

                        Profile removedByProfile = ProfileUtil.getProfile(grant.getRemovedByUUID()).get(10, TimeUnit.SECONDS);
                        if(removedByProfile != null)
                            removedBy = removedByProfile.getName();
                    }

                    List<Component> lore = new ArrayList<>();

                    lore.add(MC.Style.MENU_SEPARATOR);

                    lore.add(Component.text().append(Component.text("Added On: ", MC.CC.GRAY.getTextColor()))
                            .append(Component.text(TimeUtil.dateToString(new Date(!isDefault ? grant.getAddedAt() : profile.getFirstSeen()),
                                    true), textColor))
                            .build());

                    lore.add(Component.text().append(Component.text("Added By: ", MC.CC.GRAY.getTextColor()))
                            .append(Component.text(addedBy, textColor))
                            .build());

                    lore.add(Component.text().append(Component.text("Added Reason: ", MC.CC.GRAY.getTextColor()))
                            .append(Component.text(grant.getAddedReason(), textColor))
                            .build());

                    if(!grant.api().isPermanent() && grant.getDuration() != -1L) {
                        if(!grant.api().isActive()) {
                            lore.add(Component.text().append(Component.text("Remaining: ", MC.CC.GRAY.getTextColor()))
                                    .append(Component.text(grant.api().getTimeRemaining(), textColor))
                                    .build());
                        }
                    }

                    lore.add(Component.text().append(Component.text("Duration: ", MC.CC.GRAY.getTextColor()))
                            .append(Component.text(grant.api().getDurationText(), textColor))
                            .build());

                    if(grant.isRemoved()) {
                        lore.add(MC.Style.MENU_SEPARATOR);
                        lore.add(Component.text().append(Component.text("Removed On: ", MC.CC.GRAY.getTextColor()))
                                .append(Component.text(TimeUtil.dateToString(new Date(grant.getRemovedAt()), true), textColor))
                                .build());

                        lore.add(Component.text().append(Component.text("Removed By: ", MC.CC.GRAY.getTextColor()))
                                .append(Component.text(removedBy, textColor))
                                .build());

                        lore.add(Component.text().append(Component.text("Removed Reason: ", MC.CC.GRAY.getTextColor()))
                                .append(Component.text(grant.getRemovedReason(), textColor))
                                .build());
                    } else {
                        if(!isDefault && !grant.api().hasExpired() && FlameAPI.canStartProcedure(player)) {
                            if(player.hasPermission("flame.rank.remove")) {
                                lore.add(MC.Style.MENU_SEPARATOR);
                                lore.add(Component.text("Click to remove this Grant", textColor));
                            }
                        }
                    }

                    lore.add(MC.Style.MENU_SEPARATOR);

                    Material material = Material.BOOK;

                    if(!grant.isRemoved() && !grant.api().hasExpired())
                        material = Material.ENCHANTED_BOOK;

                    var title = !isDefault ? grant.api().getRank().getName() + " Grant (" + grant.getId() + ")" : "Default Grant";
                    items[i] = ClickableItem.of(new ItemFactory(material)
                                    .setName(Component.text(title, textColor))
                                    .setLore(lore)
                                    .build(),
                            e -> {
                                if(!grant.isRemoved() && !grant.api().hasExpired() && !isDefault && FlameAPI.canStartProcedure(player)) {
                                    if(player.hasPermission("flame.grants.remove")) {
                                        GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedure.Type.REMOVE, GrantProcedure.Stage.REQUIRE_REASON);
                                        procedure.setGrant(grant);

                                        player.sendMessage(Component.text("Type a reason for removing this grant in chat...", MC.CC.GREEN.getTextColor()));
                                        player.closeInventory();
                                    }
                                }
                            });
                    i++;
                } catch(InterruptedException | ExecutionException | TimeoutException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }

            pagination.setItems(items);
            pagination.setItemsPerPage(27);

            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

            Util.addNavigation(player, this.inventory, contents);
        }, 0);
    }

}