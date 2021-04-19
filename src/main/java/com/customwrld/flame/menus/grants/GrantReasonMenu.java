package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureStage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GrantReasonMenu implements InventoryProvider {

    private SmartInventory inventory;
    private Profile profile;
    private GrantProcedure procedure;


    public GrantReasonMenu(Player player, Profile profile, GrantProcedure procedure) {
        this.profile = profile;
        this.procedure = procedure;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title(CC.WHITE + "\uF801\uE8CB\uF802\uE8C6\uF807GRANTREASONMENU")
                .build();
        this.inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Grant grant = procedure.getGrant();

        contents.set(0, 1, ClickableItem.of(new ItemBuilder(Material.LIME_WOOL)
                        .title(CC.GREEN + "Promoted").build(),
                e -> {
                    grant.setAddedReason("Promoted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_CONFIRMATION);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new ConfirmGrantMenu(player, profile, procedure);
                }));

        contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.YELLOW_WOOL)
                        .title(CC.YELLOW + "Demoted").build(),
                e -> {
                    grant.setAddedReason("Demoted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_CONFIRMATION);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new ConfirmGrantMenu(player, profile, procedure);
                }));

        contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.ORANGE_WOOL)
                        .title(CC.GOLD + "Granted").build(),
                e -> {

                    grant.setAddedReason("Granted");
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_CONFIRMATION);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new ConfirmGrantMenu(player, profile, procedure);
                }));

        contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.RED_WOOL)
                        .title(CC.RED + "Custom").build(),
                e -> {
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.PROVIDE_CUSTOM_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    player.closeInventory();
                    player.sendMessage(CC.GREEN + "Type a reason for adding this grant in chat...");
                }));

    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}