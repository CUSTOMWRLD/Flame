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
import com.customwrld.flame.util.Duration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GrantTimeMenu implements InventoryProvider {

    private SmartInventory inventory;
    private Profile profile;
    private GrantProcedure procedure;


    public GrantTimeMenu(Player player, Profile profile, GrantProcedure procedure) {
        this.profile = profile;
        this.procedure = procedure;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title(CC.WHITE + "\uF801\uE8CB\uF802\uE8C5\uF807GRANTTIMEMENU")
                .build();
        this.inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Grant grant = procedure.getGrant();

        contents.set(0, 1, ClickableItem.of(new ItemBuilder(Material.LIME_WOOL)
                        .title(CC.GREEN + "7 Days")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("7d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.YELLOW_WOOL)
                        .title(CC.YELLOW + "30 Days")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("30d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.ORANGE_WOOL)
                        .title(CC.GOLD + "Permanent")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("perm");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedureStage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.RED_WOOL)
                        .title(CC.RED + "Custom")
                        .build(),
                e -> {
                    procedure.setStage(GrantProcedureStage.PROVIDE_CUSTOM_TIME);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    player.closeInventory();
                    player.sendMessage(CC.GREEN + "Type the amount of time you would like to grant this rank for...");
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}