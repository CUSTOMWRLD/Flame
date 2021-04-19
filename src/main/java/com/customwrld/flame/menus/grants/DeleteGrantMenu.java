package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.flame.pigeon.payloads.GrantDeletePayload;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeleteGrantMenu implements InventoryProvider {

    private SmartInventory inventory;
    private GrantProcedure grantProcedure;
    private String removeReason;

    public DeleteGrantMenu(Player player, GrantProcedure grantProcedure, String removeReason) {
        this.grantProcedure = grantProcedure;
        this.removeReason = removeReason;
        this.inventory = SmartInventory.builder()
                .id("deleteGrantMenu")
                .provider(this)
                .size(1, 9)
                .title("Remove Grant?")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .title(" ")
                .build()));

        for (int x = 0; x < 4; x++) {
            contents.set(0, x, ClickableItem.of(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                            .title(CC.GREEN + "Remove Grant")
                            .build(),
                    e -> {
                        grantProcedure.getGrant().setRemovedBy(player.getUniqueId());
                        grantProcedure.getGrant().setRemovedAt(System.currentTimeMillis());
                        grantProcedure.getGrant().setRemovedReason(removeReason);
                        grantProcedure.getGrant().setRemoved(true);
                        grantProcedure.finish();
                        inventory.close(player);
                        player.sendMessage(CC.GREEN + "The grant has been removed.");
                        new GrantDeletePayload(grantProcedure.getRecipient().getUuid(), grantProcedure.getGrant()).send();
                    }));
            contents.set(0, 5 + x, ClickableItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .title(CC.RED + "Cancel Removal")
                            .build(),
                    e -> {
                        inventory.close(player);
                        grantProcedure.cancel();
                        player.sendMessage(CC.RED + "Cancelled the grant procedure.");
                    }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
