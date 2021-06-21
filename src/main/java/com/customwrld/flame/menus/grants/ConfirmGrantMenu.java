package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.SlotPos;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.flame.profile.grant.GrantProcedure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmGrantMenu implements InventoryProvider {

    private final SmartInventory inventory;
    private final GrantProcedure procedure;

    public ConfirmGrantMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Grant Confirmation")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Profile profile = this.procedure.getRecipient();
        Grant grant = this.procedure.getGrant();
        Rank rank = this.procedure.getGrant().api().getRank();

        for (int i = 1; i <= 3; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemFactory(Material.LIME_CONCRETE)
                    .setName(Component.text("Confirm Grant Procedure")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false))
                    .build(), e -> {

                grant.setAddedAt(System.currentTimeMillis());

                profile.api().addGrant(grant);

                this.procedure.finish();
                this.inventory.close(player);

                player.sendMessage(CC.GREEN + "You have updated " + profile.getName() + CC.GREEN + "'s rank to " + rank.getName());
            }));

        for (int i = 5; i <= 7; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemFactory(Material.RED_CONCRETE)
                    .setName(Component.text("Cancel Grant Procedure")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false))
                    .build(), e -> {

                procedure.cancel();
                inventory.close(player);

                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }));
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_CONFIRMATION) {
            procedure.cancel();
            player.sendMessage(CC.RED + "Cancelled the grant procedure.");
        }
    }

}
