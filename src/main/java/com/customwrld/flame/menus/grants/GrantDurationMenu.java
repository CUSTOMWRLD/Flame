package com.customwrld.flame.menus.grants;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.util.Duration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GrantDurationMenu implements InventoryProvider {

    private final GrantProcedure procedure;


    public GrantDurationMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        SmartInventory inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title("Grant Duration")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Grant grant = procedure.getGrant();

        contents.set(0, 1, ClickableItem.of(new ItemFactory(Material.LIME_WOOL)
                        .setName(Component.text("7 Days")
                                .color(NamedTextColor.GREEN)
                                .decoration(TextDecoration.ITALIC, false))
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("7d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, procedure);
                }));

        contents.set(0, 3, ClickableItem.of(new ItemFactory(Material.YELLOW_WOOL)
                        .setName(Component.text("30 Days")
                                .color(NamedTextColor.YELLOW)
                                .decoration(TextDecoration.ITALIC, false))
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("30d");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, procedure);
                }));

        contents.set(0, 5, ClickableItem.of(new ItemFactory(Material.ORANGE_WOOL)
                        .setName(Component.text("Permanent")
                                .color(NamedTextColor.GOLD)
                                .decoration(TextDecoration.ITALIC, false))
                        .build(),
                e -> {
                    Duration duration = Duration.fromString("perm");

                    grant.setDuration(duration.getValue());
                    procedure.setGrant(grant);

                    procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, procedure);
                }));

        contents.set(0, 7, ClickableItem.of(new ItemFactory(Material.RED_WOOL)
                        .setName(Component.text("Custom")
                                .color(NamedTextColor.RED)
                                .decoration(TextDecoration.ITALIC, false))
                        .build(),
                e -> {
                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CUSTOM_TIME);

                    player.closeInventory();
                    player.sendMessage(Component.text("Type the amount of time you would like to grant this rank for...")
                            .color(MC.CC.GREEN.getTextColor()));
                }));
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_TIME) {
            procedure.cancel();
            player.sendMessage(Component.text("Cancelled the grant procedure.")
                    .color(MC.CC.RED.getTextColor()));
        }
    }

}