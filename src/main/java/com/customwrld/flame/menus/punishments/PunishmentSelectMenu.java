package com.customwrld.flame.menus.punishments;

import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
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

        contents.set(1, 1, ClickableItem.of(new ItemFactory(Material.RED_CONCRETE)
                        .setName(Component.text(Punishment.Type.BLACKLIST.getTypeData().getReadable())
                                .color(TextColor.color(Punishment.Type.BLACKLIST.getTypeData().getColor().getRGB()))
                                .decoration(TextDecoration.BOLD, true))
                        .setLore(Component.text(this.profile.api().getPunishmentCountByType(Punishment.Type.BLACKLIST) + " " +
                                Punishment.Type.BLACKLIST.getTypeData().getReadable() + " on record."))
                        .build(),
                e -> {
                    if (!(this.profile.api().getPunishmentCountByType(Punishment.Type.BLACKLIST) < 1)) {
                        new PunishmentsListMenu(player, this.profile, Punishment.Type.BLACKLIST);
                    }
                }));

        contents.set(1, 3, ClickableItem.of(new ItemFactory(Material.YELLOW_CONCRETE)
                        .setName(Component.text(Punishment.Type.BAN.getTypeData().getReadable())
                                .color(TextColor.color(Punishment.Type.BAN.getTypeData().getColor().getRGB()))
                                .decoration(TextDecoration.BOLD, true))
                        .setLore(Component.text(this.profile.api().getPunishmentCountByType(Punishment.Type.BAN) + " " +
                                Punishment.Type.BAN.getTypeData().getReadable() + " on record."))
                        .build(),
                e -> {
                    if (!(this.profile.api().getPunishmentCountByType(Punishment.Type.BAN) < 1)) {
                        new PunishmentsListMenu(player, this.profile, Punishment.Type.BAN);
                    }
                }));

        contents.set(1, 5, ClickableItem.of(new ItemFactory(Material.LIME_CONCRETE)
                        .setName(Component.text(Punishment.Type.MUTE.getTypeData().getReadable())
                                .color(TextColor.color(Punishment.Type.MUTE.getTypeData().getColor().getRGB()))
                                .decoration(TextDecoration.BOLD, true))
                        .setLore(Component.text(this.profile.api().getPunishmentCountByType(Punishment.Type.MUTE) + " " +
                                Punishment.Type.MUTE.getTypeData().getReadable() + " on record."))
                        .build(),
                e -> {
                    if (!(this.profile.api().getPunishmentCountByType(Punishment.Type.MUTE) < 1)) {
                        new PunishmentsListMenu(player, this.profile, Punishment.Type.MUTE);
                    }
                }));

        contents.set(1, 7, ClickableItem.of(new ItemFactory(Material.LIGHT_BLUE_CONCRETE)
                        .setName(Component.text(Punishment.Type.WARN.getTypeData().getReadable())
                                .color(TextColor.color(Punishment.Type.WARN.getTypeData().getColor().getRGB()))
                                .decoration(TextDecoration.BOLD, true))
                        .setLore(Component.text(this.profile.api().getPunishmentCountByType(Punishment.Type.WARN) + " " +
                                Punishment.Type.WARN.getTypeData().getReadable() + " on record."))
                        .build(),
                e -> {
                    if (!(this.profile.api().getPunishmentCountByType(Punishment.Type.WARN) < 1)) {
                        new PunishmentsListMenu(player, this.profile, Punishment.Type.WARN);
                    }
                }));
    }

}
