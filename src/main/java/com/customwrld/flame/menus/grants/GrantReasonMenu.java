package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.profile.grant.GrantProcedure;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GrantReasonMenu implements InventoryProvider {

    private final GrantProcedure procedure;

    public GrantReasonMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        SmartInventory inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Grant Reason")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Profile profile = this.procedure.getRecipient();
        Grant grant = this.procedure.getGrant();

        contents.set(0, 1, ClickableItem.of(new ItemFactory(Material.LIME_WOOL)
                        .setName(Component.text("Promoted", MC.CC.GREEN.getTextColor()))
                        .build(),
                e -> {
                    grant.setAddedReason("Promoted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);

                    new ConfirmGrantMenu(player, procedure);
                }));

        contents.set(0, 3, ClickableItem.of(new ItemFactory(Material.YELLOW_WOOL)
                        .setName(Component.text("Demoted", MC.CC.YELLOW.getTextColor()))
                        .build(),
                e -> {
                    grant.setAddedReason("Demoted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);

                    new ConfirmGrantMenu(player, procedure);
                }));

        contents.set(0, 5, ClickableItem.of(new ItemFactory(Material.ORANGE_WOOL)
                        .setName(Component.text("Granted", MC.CC.GOLD.getTextColor()))
                        .build(),
                e -> {

                    grant.setAddedReason("Granted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);

                    new ConfirmGrantMenu(player, procedure);
                }));

        contents.set(0, 7, ClickableItem.of(new ItemFactory(Material.RED_WOOL)
                        .setName(Component.text("Custom", MC.CC.RED.getTextColor()))
                        .build(),
                e -> {
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CUSTOM_REASON);

                    player.closeInventory();
                    player.sendMessage(Component.text("Type a reason for adding this grant in chat...", MC.CC.GREEN.getTextColor()));
                }));

    }

    @Override
    public void close(Player player, InventoryContents contents) {
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_REASON) {
            procedure.cancel();
            player.sendMessage(Component.text("Cancelled the grant procedure.", MC.CC.RED.getTextColor()));
        }
    }

}