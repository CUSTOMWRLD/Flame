package com.customwrld.flame.menus.punishments;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeletePunishmentMenu implements InventoryProvider {

    private SmartInventory inventory;
    private PunishmentProcedure procedure;
    private String reason;

    public DeletePunishmentMenu(Player player, PunishmentProcedure procedure, String reason) {
        this.procedure = procedure;
        this.reason = reason;
        this.inventory = SmartInventory.builder()
                .id("deletePunishmentMenu")
                .provider(this)
                .size(1, 9)
                .title("Remove Punishment?").build();
        inventory.open(player);
        inventory.setCloseable(false);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .title(" ")
                .build()));

        for (int x = 0; x < 4; x++) {
            contents.set(0, x, ClickableItem.of(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                            .title(CC.GREEN + "Confirm Punishment Removal")
                            .build(),
                    e -> {
                        procedure.getPunishment().setRemovedBy(player.getUniqueId());
                        procedure.getPunishment().setRemovedAt(System.currentTimeMillis());
                        procedure.getPunishment().setRemovedReason(reason);
                        procedure.getPunishment().setRemoved(true);
                        procedure.finish();
                        inventory.close(player);
                        player.sendMessage(CC.GREEN + "The punishment has been pardoned.");
                    }));
            contents.set(0, 5 + x, ClickableItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .title(CC.RED + "Cancel Punishment Removal")
                            .build(),
                    e -> {
                        inventory.close(player);
                        procedure.cancel();
                        player.sendMessage(CC.RED + "Cancelled the punishment procedure.");
                    }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
