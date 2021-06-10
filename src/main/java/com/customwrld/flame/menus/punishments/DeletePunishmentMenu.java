package com.customwrld.flame.menus.punishments;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Util;
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

        contents.fill(ClickableItem.empty(Util.FILLER));

        for (int x = 0; x < 4; x++) {
            contents.set(0, x, ClickableItem.of(new ItemFactory(Material.LIME_STAINED_GLASS_PANE)
                            .setName(CC.GREEN + "Confirm Punishment Removal")
                            .build(),
                    e -> {
                        procedure.getPunishment().setRemovedBy(player.getUniqueId());
                        procedure.getPunishment().setRemovedAt(System.currentTimeMillis());
                        procedure.getPunishment().setRemovedReason(reason);
                        procedure.getPunishment().setRemoved(true);
                        procedure.getPunishment().save();
                        procedure.finish();
                        inventory.close(player);
                        player.sendMessage(CC.GREEN + "The punishment has been pardoned.");
                    }));
            contents.set(0, 5 + x, ClickableItem.of(new ItemFactory(Material.RED_STAINED_GLASS_PANE)
                            .setName(CC.RED + "Cancel Punishment Removal")
                            .build(),
                    e -> {
                        inventory.close(player);
                        procedure.cancel();
                        player.sendMessage(CC.RED + "Cancelled the punishment procedure.");
                    }));
        }
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(player);

        if (punishmentProcedure != null) {
            punishmentProcedure.cancel();
            player.sendMessage(CC.RED + "Cancelled the grant procedure.");
        }
    }

}
