package com.customwrld.flame.menus.punishments;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Util;
import org.bukkit.entity.Player;

public class PunishmentSelectMenu implements InventoryProvider {

    private final Profile profile;

    public PunishmentSelectMenu(Player player, Profile profile) {
        this.profile = profile;
        SmartInventory inventory = SmartInventory.builder()
                .provider(this)
                .size(3, 9)
                .title(profile.getName() + "'s Punishments").build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(Util.FILLER));

        contents.set(1, 1, ClickableItem.of(new ItemFactory(Punishment.Type.BLACKLIST.getTypeData().getMaterial())
                        .setName(Punishment.Type.BLACKLIST.getTypeData().getColor() + CC.BOLD + Punishment.Type.BLACKLIST.getTypeData().getReadable())
                        .setLore(CC.GRAY + profile.getPunishmentCountByType(Punishment.Type.BLACKLIST) + " " + Punishment.Type.BLACKLIST.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(Punishment.Type.BLACKLIST) < 1)) {
                        new PunishmentsListMenu(player, profile, Punishment.Type.BLACKLIST);
                    }
                }));

        contents.set(1, 3, ClickableItem.of(new ItemFactory(Punishment.Type.BAN.getTypeData().getMaterial())
                        .setName(Punishment.Type.BAN.getTypeData().getColor() + CC.BOLD + Punishment.Type.BAN.getTypeData().getReadable())
                        .setLore(CC.GRAY + profile.getPunishmentCountByType(Punishment.Type.BAN) + " " + Punishment.Type.BAN.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(Punishment.Type.BAN) < 1)) {
                        new PunishmentsListMenu(player, profile, Punishment.Type.BAN);
                    }
                }));

        contents.set(1, 5, ClickableItem.of(new ItemFactory(Punishment.Type.MUTE.getTypeData().getMaterial())
                        .setName(Punishment.Type.MUTE.getTypeData().getColor() + CC.BOLD + Punishment.Type.MUTE.getTypeData().getReadable())
                        .setLore(CC.GRAY + profile.getPunishmentCountByType(Punishment.Type.MUTE) + " " + Punishment.Type.MUTE.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(Punishment.Type.MUTE) < 1)) {
                        new PunishmentsListMenu(player, profile, Punishment.Type.MUTE);
                    }
                }));

        contents.set(1, 7, ClickableItem.of(new ItemFactory(Punishment.Type.WARN.getTypeData().getMaterial())
                        .setName(Punishment.Type.WARN.getTypeData().getColor() + CC.BOLD + Punishment.Type.WARN.getTypeData().getReadable())
                        .setLore(CC.GRAY + profile.getPunishmentCountByType(Punishment.Type.WARN) + " " + Punishment.Type.WARN.getTypeData().getReadable() + " on record.")
                        .build(),
                e -> {
                    if (!(profile.getPunishmentCountByType(Punishment.Type.WARN) < 1)) {
                        new PunishmentsListMenu(player, profile, Punishment.Type.WARN);
                    }
                }));
    }

}
