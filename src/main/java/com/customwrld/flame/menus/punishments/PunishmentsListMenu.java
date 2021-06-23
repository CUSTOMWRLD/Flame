package com.customwrld.flame.menus.punishments;

import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.commonlib.util.TimeUtil;
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
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PunishmentsListMenu implements InventoryProvider {

    private final Profile profile;
    private final Punishment.Type type;
    private final SmartInventory inventory;

    PunishmentsListMenu(Player player, Profile profile, Punishment.Type type) {
        this.profile = profile;
        this.type = type;
        inventory = SmartInventory.builder()
                .provider(this)
                .size(5, 9)
                .title(profile.getName() + "'s " + type.getTypeData().getReadable()).build();
        inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[this.profile.api().getPunishmentCountByType(this.type)];

        contents.fill(ClickableItem.empty(Util.FILLER));

        int i = 0;

        List<Punishment> sortedPunishments = new ArrayList<>(this.profile.getCachedPunishments());
        sortedPunishments.sort((punishment1, punishment2) -> Boolean.compare(punishment1.isRemoved(), punishment2.isRemoved()));

        for (Punishment punishment : sortedPunishments) {
            if (punishment.getType() == this.type) {
                String addedBy = "Console";

                if (punishment.getAddedByUUID() != null) {
                    addedBy = "Could not fetch...";

                    Profile addedByProfile = Profile.getProfile(null, punishment.getAddedByUUID());

                    if (addedByProfile != null) {
                        addedBy = addedByProfile.getName();
                    }
                }

                String removedBy = "Console";

                if (punishment.getRemovedByUUID() != null) {
                    removedBy = "Could not fetch...";

                    Profile removedByProfile = Profile.getProfile(null, punishment.getRemovedByUUID());

                    if (removedByProfile != null) {
                        removedBy = removedByProfile.getName();
                    }
                }

                List<Component> lore = new ArrayList<>();
                lore.add(Minecraft.Style.MENU_SEPARATOR);
                lore.add(Component.text().append(Component.text("Added By: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(addedBy)
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text().append(Component.text("Added Reason: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(punishment.getAddedReason())
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));

                if (!punishment.api().isPermanent() && punishment.getDuration() != -1L) {
                    if (punishment.api().isActive()) {
                        lore.add(Component.text().append(Component.text("Remaining: ")
                                .color(NamedTextColor.GRAY))
                                .append(Component.text(punishment.api().getTimeRemaining())
                                        .color(NamedTextColor.DARK_AQUA))
                                .build()
                                .decoration(TextDecoration.ITALIC, false));
                    }
                }

                lore.add(Component.text().append(Component.text("Duration: ")
                        .color(NamedTextColor.GRAY))
                        .append(Component.text(punishment.api().getDurationText())
                                .color(NamedTextColor.DARK_AQUA))
                        .build()
                        .decoration(TextDecoration.ITALIC, false));

                if (punishment.isRemoved()) {
                    lore.add(Minecraft.Style.MENU_SEPARATOR);
                    lore.add(Component.text().append(Component.text("Removed On: ")
                            .color(NamedTextColor.GRAY))
                            .append(Component.text(TimeUtil.dateToString(new Date(punishment.getRemovedAt())))
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
                            .append(Component.text(punishment.getRemovedReason())
                                    .color(NamedTextColor.DARK_AQUA))
                            .build()
                            .decoration(TextDecoration.ITALIC, false));
                } else {
                    if (!punishment.api().hasExpired() && punishment.getType().isRemovable() && FlameAPI.canStartProcedure(player)) {
                        lore.add(Minecraft.Style.MENU_SEPARATOR);

                        if (player.hasPermission("flame.punishment.remove")) {
                            lore.add(Component.text("Click to pardon this punishment")
                                    .color(NamedTextColor.DARK_AQUA));
                        }
                    }
                }

                lore.add(Minecraft.Style.MENU_SEPARATOR);

                Material material = Material.BOOK;

                if (!punishment.isRemoved() && !punishment.api().hasExpired()) {
                    material = Material.ENCHANTED_BOOK;
                }

                items[i] = ClickableItem.of(new ItemFactory(material)
                                .setName(Component.text(TimeUtil.dateToString(new Date(punishment.getAddedAt()))).color(NamedTextColor.DARK_AQUA))
                                .setLore(lore)
                                .build(),
                        e -> {
                            if (!punishment.isRemoved() && !punishment.api().hasExpired() && punishment.getType().isRemovable() && player.hasPermission("flame.punishment.remove") && FlameAPI.canStartProcedure(player)) {
                                PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedure.Type.REMOVE, PunishmentProcedure.Stage.REQUIRE_TEXT);
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

        Util.addNavigation(player, this.inventory, contents);
    }

}
