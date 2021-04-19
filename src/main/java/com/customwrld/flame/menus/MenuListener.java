package com.customwrld.flame.menus;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureStage;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String name = event.getView().getTitle();

        if (name.contains("GRANTTIMEMENU")) {
            GrantProcedure procedure = GrantProcedure.getByPlayer(player);
            if (procedure != null && procedure.getStage() == GrantProcedureStage.REQUIRE_TIME) {
                procedure.cancel();
                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }
        }

        if (name.contains("GRANTREASONMENU")) {
            GrantProcedure procedure = GrantProcedure.getByPlayer(player);
            if (procedure != null && procedure.getStage() == GrantProcedureStage.REQUIRE_REASON) {
                procedure.cancel();
                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }
        }

        if (name.contains("GRANTCONFIRMMENU")) {
            GrantProcedure procedure = GrantProcedure.getByPlayer(player);
            if (procedure != null && procedure.getStage() == GrantProcedureStage.REQUIRE_CONFIRMATION) {
                procedure.cancel();
                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }
        }

        if (event.getView().getTitle().equalsIgnoreCase("REMOVEPUNISHMENTCONFIRMMENU")) {
            PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(player);

            if (punishmentProcedure != null) {
                punishmentProcedure.cancel();
                player.sendMessage(CC.RED + "Cancelled the grant procedure.");
            }
        }
    }

}
