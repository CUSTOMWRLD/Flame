package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.SlotPos;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.flame.pigeon.payloads.GrantAddPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.rank.Rank;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ConfirmGrantMenu implements InventoryProvider {

    private SmartInventory inventory;
    private Profile profile;
    private GrantProcedure procedure;

    public ConfirmGrantMenu(Player player, Profile profile, GrantProcedure procedure) {
        this.profile = profile;
        this.procedure = procedure;
        this.inventory = SmartInventory.builder()
                .provider(this)
                .size(1, 9)
                .title(CC.WHITE + "\uF801\uE8CA\uF802\uE8C0\uF807GRANTCONFIRMMENU")
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Rank rank = procedure.getGrant().getRank();

        for (int i = 1; i <= 3; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemBuilder()
                    .material(Material.PAPER)
                    .title("&aConfirm Grant Procedure")
                    .modelData(1)
                    .build(), e -> {
                Grant grant = procedure.getGrant();

                grant.setAddedAt(System.currentTimeMillis());

                profile.getGrants().add(grant);
                profile.save();

                new GrantAddPayload(procedure.getRecipient().getUuid(), procedure.getGrant()).send();

                procedure.finish();
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                inventory.close(player);
                player.sendMessage(CC.GREEN + "You have updated " + profile.getName() + CC.GREEN + " rank to " + rank.getName());
            }));

        for (int i = 5; i <= 7; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemBuilder()
                    .material(Material.PAPER)
                    .title("&cCancel Grant Procedure")
                    .modelData(1)
                    .build(), e -> {
                procedure.cancel();
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                inventory.close(player);
                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
