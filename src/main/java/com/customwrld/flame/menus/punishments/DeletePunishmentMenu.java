package com.customwrld.flame.menus.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeletePunishmentMenu implements InventoryProvider {

    private final SmartInventory inventory;
    private final PunishmentProcedure procedure;
    private final String reason;

    public DeletePunishmentMenu(Player player, PunishmentProcedure procedure, String reason) {
        this.procedure = procedure;
        this.reason = reason;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Punishment Removal")
                .build();
        inventory.open(player);
        inventory.setCloseable(false);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Profile profile = this.procedure.getRecipient();
        Punishment punishment = this.procedure.getPunishment();

        contents.fill(ClickableItem.empty(Util.FILLER));

        for (int x = 0; x < 4; x++) {
            contents.set(0, x, ClickableItem.of(new ItemFactory(Material.LIME_STAINED_GLASS_PANE)
                            .setName(Component.text("Confirm Punishment Removal Procedure")
                                    .color(NamedTextColor.GREEN)
                                    .decoration(TextDecoration.ITALIC, false))
                            .build(),
                    e -> {
                        profile.api().removePunishment(punishment, player.getUniqueId(), System.currentTimeMillis(), this.reason);

                        procedure.finish();
                        inventory.close(player);
                        player.sendMessage(Component.text("The punishment has been pardoned.")
                                .color(MC.CC.GREEN.getTextColor()));
                    }));
            contents.set(0, 5 + x, ClickableItem.of(new ItemFactory(Material.RED_STAINED_GLASS_PANE)
                            .setName(Component.text("Cancel Punishment Removal Procedure")
                                    .color(NamedTextColor.GREEN)
                                    .decoration(TextDecoration.ITALIC, false))
                            .build(),
                    e -> {
                        inventory.close(player);
                        procedure.cancel();
                        player.sendMessage(Component.text("Cancelled the punishment procedure.")
                                .color(MC.CC.RED.getTextColor()));
                    }));
        }
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(player);

        if (punishmentProcedure != null) {
            punishmentProcedure.cancel();
            player.sendMessage(Component.text("Cancelled the punishment procedure.")
                    .color(MC.CC.RED.getTextColor()));
        }
    }

}
