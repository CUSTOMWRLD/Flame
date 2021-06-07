package com.customwrld.flame.menus.punishments;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedureStage;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedureType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PunishmentsListMenu implements InventoryProvider {

    private final Profile profile;
    private final PunishmentType punishmentType;
    private final SmartInventory inventory;

    PunishmentsListMenu(Player player, Profile profile, PunishmentType punishmentType) {
        this.profile = profile;
        this.punishmentType = punishmentType;
        inventory = SmartInventory.builder()
                .id("punishmentsListMenu")
                .provider(this)
                .size(5, 9)
                .title(profile.getName() + "'s " + punishmentType.getTypeData().getReadable()).build();
        inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[profile.getPunishmentCountByType(punishmentType)];

        contents.fill(ClickableItem.empty(new ItemFactory(Material.GRAY_STAINED_GLASS_PANE).title(" ").build()));

        int i = 0;

        List<Punishment> sortedPunishments = new ArrayList<>(profile.getPunishments());
        sortedPunishments.sort((punishment1, punishment2) -> Boolean.compare(punishment1.isRemoved(), punishment2.isRemoved()));

        for (Punishment punishment : sortedPunishments) {
            if (punishment.getType() == punishmentType) {
                profile.save();

                String addedBy = "Console";

                if (punishment.getAddedBy() != null) {
                    addedBy = "Could not fetch...";

                    Profile addedByProfile = Profile.getByUuid(punishment.getAddedBy());

                    if (addedByProfile != null && addedByProfile.isLoaded()) {
                        addedBy = addedByProfile.getName();
                    }
                }

                String removedBy = "Console";

                if (punishment.getRemovedBy() != null) {
                    removedBy = "Could not fetch...";

                    Profile removedByProfile = Profile.getByUuid(punishment.getRemovedBy());

                    if (removedByProfile != null && removedByProfile.isLoaded()) {
                        removedBy = removedByProfile.getName();
                    }
                }

                List<String> lore = new ArrayList<>();
                lore.add(CC.MENU_BAR);
                lore.add("&eAdded By: &c" + addedBy);
                lore.add("&eAdded Reason: &c" + punishment.getAddedReason());

                if (!punishment.isPermanent() && punishment.getDuration() != -1L) {
                    if (punishment.isActive()) {
                        lore.add("&eRemaining: &c" + punishment.getTimeRemaining());
                    }
                }

                lore.add("&eDuration: &c" + punishment.getDurationText());

                if (punishment.isRemoved()) {
                    lore.add(CC.MENU_BAR);
                    lore.add("&eRemoved By: &c" + removedBy);
                    lore.add("&eRemoved On: &c" + TimeUtil.dateToString(new Date(punishment.getRemovedAt())));
                    lore.add("&eRemoved Reason: &c" + punishment.getRemovedReason());
                } else {
                    if (!punishment.hasExpired() && punishment.getType().isRemovable() && FlameAPI.canStartProcedure(player)) {
                        lore.add(CC.MENU_BAR);

                        if (player.hasPermission("flame.punishment.remove")) {
                            lore.add("&6Click to pardon this punishment");
                        }
                    }
                }

                lore.add(CC.MENU_BAR);

                Material material = Material.BOOK;

                if (!punishment.isRemoved() && !punishment.hasExpired()) {
                    material = Material.ENCHANTED_BOOK;
                }

                items[i] = ClickableItem.of(new ItemFactory(material)
                                .title(CC.GOLD + TimeUtil.dateToString(new Date(punishment.getAddedAt())))
                                .lores(lore)
                                .build(),
                        e -> {
                            if (!punishment.isRemoved() && !punishment.hasExpired() && punishment.getType().isRemovable() && player.hasPermission("flame.punishment.remove") && FlameAPI.canStartProcedure(player)) {
                                PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedureType.PARDON, PunishmentProcedureStage.REQUIRE_TEXT);
                                procedure.setPunishment(punishment);

                                player.sendMessage(CC.GREEN + "Type a reason for pardoning this punishment in chat...");
                                player.closeInventory();
                            }
                        });
                i++;
            }
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.set(0, 0, ClickableItem.of(new ItemFactory(Material.ARROW)
                        .title("&6Previous Page")
                        .build(),
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(new ItemFactory(Material.ARROW)
                        .title("&6Next Page")
                        .build(),
                e -> inventory.open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
