package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeleteGrantMenu implements InventoryProvider {

    private final SmartInventory inventory;
    private final GrantProcedure procedure;
    private final String removeReason;

    public DeleteGrantMenu(Player player, GrantProcedure procedure, String removeReason) {
        this.procedure = procedure;
        this.removeReason = removeReason;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Grant Removal")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Profile profile = this.procedure.getRecipient();

        contents.fill(ClickableItem.empty(Util.FILLER));

        for (int x = 0; x < 4; x++) {
            contents.set(0, x, ClickableItem.of(new ItemFactory(Material.LIME_CONCRETE)
                            .setName(Component.text("Confirm Grant Removal Procedure", MC.CC.GREEN.getTextColor()))
                            .build(),
                    e -> {
                        profile.api().removeGrant(procedure.getGrant(), player.getUniqueId(), System.currentTimeMillis(), this.removeReason);

                        procedure.finish();
                        inventory.close(player);
                        player.sendMessage(Component.text("The grant has been removed.", MC.CC.GREEN.getTextColor()));
                    }));
            contents.set(0, 5 + x, ClickableItem.of(new ItemFactory(Material.RED_CONCRETE)
                            .setName(Component.text("Cancel Grant Removal Procedure", MC.CC.RED.getTextColor()))
                            .build(),
                    e -> {
                        inventory.close(player);
                        procedure.cancel();
                        player.sendMessage(Component.text("Cancelled the grant procedure.", MC.CC.RED.getTextColor()));
                    }));
        }
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_CONFIRMATION) {
            procedure.cancel();
            player.sendMessage(Component.text("Cancelled the grant removal procedure.", MC.CC.RED.getTextColor()));
        }
    }

}
