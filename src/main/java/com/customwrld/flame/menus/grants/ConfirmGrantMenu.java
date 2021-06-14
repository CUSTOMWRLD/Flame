package com.customwrld.flame.menus.grants;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.SlotPos;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.grant.Grant;;
import com.customwrld.commonlib.modules.grant.Grant;Procedure;
import com.customwrld.flame.profile.rank.Rank;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmGrantMenu implements InventoryProvider {

    private final SmartInventory inventory;
    private final Profile profile;
    private final GrantProcedure procedure;

    public ConfirmGrantMenu(Player player, Profile profile, GrantProcedure procedure) {
        this.profile = profile;
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

        Rank rank = procedure.getGrant().getRank();

        for (int i = 1; i <= 3; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemFactory(Material.PAPER)
                    .setName("&aConfirm Grant Procedure")
                    .build(), e -> {
                Grant grant = procedure.getGrant();

                grant.setAddedAt(System.currentTimeMillis());

                this.profile.getGrants().add(grant.getId());
                this.profile.save();

                if (Profile.getProfiles().containsKey(profile.getUuid())) {
//                    profile.reload(); // TODO: RELOAD
                } else {
//                    CustomLib.getCustomLib().getPigeon().broadcast(new RecalculateProfilePayload(profile.getUuid())); // TODO: RELOAD
                }

                this.procedure.finish();
                this.inventory.close(player);

                player.sendMessage(CC.GREEN + "You have updated " + profile.getName() + CC.GREEN + " rank to " + rank.getName());
            }));

        for (int i = 5; i <= 7; i++)
            contents.set(SlotPos.of(0, i), ClickableItem.of(new ItemFactory(Material.PAPER)
                    .setName("&cCancel Grant Procedure")
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
