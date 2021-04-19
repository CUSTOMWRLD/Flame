package com.customwrld.flame.menus.punishments;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishmentSelectMenu implements InventoryProvider {

    private final Profile profile;

    public PunishmentSelectMenu(Player player, Profile profile) {
        this.profile = profile;
        SmartInventory inventory = SmartInventory.builder()
                .id("punishmentSelectMenu")
                .provider(this)
                .size(3, 9)
                .title(profile.getName() + "'s Punishments").build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .title(" ")
                .build()));

        contents.set(1, 2, ClickableItem.of(new ItemBuilder(PunishmentType.BLACKLIST.getTypeData().getMaterial())
                        .title(PunishmentType.BLACKLIST.getTypeData().getColor() + CC.BOLD + PunishmentType.BLACKLIST.getTypeData().getReadable())
                        .lores(CC.GRAY + profile.getPunishmentCountByType(PunishmentType.BLACKLIST) + " " + PunishmentType.BLACKLIST.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(PunishmentType.BLACKLIST) < 1)) {
                        new PunishmentsListMenu(player, profile, PunishmentType.BLACKLIST);
                    }
                }));

        contents.set(1, 3, ClickableItem.of(new ItemBuilder(PunishmentType.BAN.getTypeData().getMaterial())
                        .title(PunishmentType.BAN.getTypeData().getColor() + CC.BOLD + PunishmentType.BAN.getTypeData().getReadable())
                        .lores(CC.GRAY + profile.getPunishmentCountByType(PunishmentType.BAN) + " " + PunishmentType.BAN.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(PunishmentType.BAN) < 1)) {
                        new PunishmentsListMenu(player, profile, PunishmentType.BAN);
                    }
                }));

        contents.set(1, 4, ClickableItem.of(new ItemBuilder(PunishmentType.MUTE.getTypeData().getMaterial())
                        .title(PunishmentType.MUTE.getTypeData().getColor() + CC.BOLD + PunishmentType.MUTE.getTypeData().getReadable())
                        .lores(CC.GRAY + profile.getPunishmentCountByType(PunishmentType.MUTE) + " " + PunishmentType.MUTE.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(PunishmentType.MUTE) < 1)) {
                        new PunishmentsListMenu(player, profile, PunishmentType.MUTE);
                    }
                }));

        contents.set(1, 5, ClickableItem.of(new ItemBuilder(PunishmentType.WARN.getTypeData().getMaterial())
                        .title(PunishmentType.WARN.getTypeData().getColor() + CC.BOLD + PunishmentType.WARN.getTypeData().getReadable())
                        .lores(CC.GRAY + profile.getPunishmentCountByType(PunishmentType.WARN) + " " + PunishmentType.WARN.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(PunishmentType.WARN) < 1)) {
                        new PunishmentsListMenu(player, profile, PunishmentType.WARN);
                    }
                }));

        contents.set(1, 6, ClickableItem.of(new ItemBuilder(PunishmentType.KICK.getTypeData().getMaterial())
                        .title(PunishmentType.KICK.getTypeData().getColor() + CC.BOLD + PunishmentType.KICK.getTypeData().getReadable())
                        .lores(CC.GRAY + profile.getPunishmentCountByType(PunishmentType.KICK) + " " + PunishmentType.KICK.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(PunishmentType.KICK) < 1)) {
                        new PunishmentsListMenu(player, profile, PunishmentType.KICK);
                    }
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
