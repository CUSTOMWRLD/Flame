package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Duration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GrantDurationMenu implements InventoryProvider {

    private final SmartInventory inventory;
    private final Profile profile;
    private final GrantProcedure procedure;


    public GrantDurationMenu(Player player, Profile profile, GrantProcedure procedure) {
        this.profile = profile;
        this.procedure = procedure;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Grant Duration")
                .build();
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Grant grant = procedure.getGrant();

        contents.set(0, 1, ClickableItem.of(new ItemFactory(Material.LIME_WOOL)
                        .setName(CC.GREEN + "7 Days")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("7d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 3, ClickableItem.of(new ItemFactory(Material.YELLOW_WOOL)
                        .setName(CC.YELLOW + "30 Days")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("30d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 5, ClickableItem.of(new ItemFactory(Material.ORANGE_WOOL)
                        .setName(CC.GOLD + "Permanent")
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("perm");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    new GrantReasonMenu(player, profile, procedure);
                }));

        contents.set(0, 7, ClickableItem.of(new ItemFactory(Material.RED_WOOL)
                        .setName(CC.RED + "Custom")
                        .build(),
                e -> {
                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CUSTOM_TIME);

                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    player.closeInventory();
                    player.sendMessage(CC.GREEN + "Type the amount of time you would like to grant this rank for...");
                }));
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_TIME) {
            procedure.cancel();
            player.sendMessage(CC.RED + "Cancelled the grant procedure.");
        }
    }

}