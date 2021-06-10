package com.customwrld.flame.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.menus.grants.ConfirmGrantMenu;
import com.customwrld.flame.menus.grants.DeleteGrantMenu;
import com.customwrld.flame.menus.grants.GrantReasonMenu;
import com.customwrld.flame.menus.punishments.DeletePunishmentMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Duration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatProcedure {

    public static void handleAuthentication(Player player, Profile profile, String message) {
        int code = Integer.parseInt(message);

        if (correctCode(profile, code)) {
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.GREEN + "Access Granted! Thank you for Authenticating!");
            player.sendMessage(CC.GREEN + "Attempt has been logged to the Discord.");
            player.sendMessage(CC.CHAT_BAR);
        } else {

            Bukkit.getScheduler().runTask(Flame.getFlame(), () -> player.kickPlayer(CC.RED + "Incorrect or Expired Two Factor Code!\nAttempt has been logged to the Discord."));
        }
    }

    private static boolean correctCode(Profile profile, int code) {
        String secretKey = profile.getStaffProfile().getTwoFactorKey();
        GoogleAuthenticator auth = Flame.getFlame().getAuth();
        boolean codeIsValid = auth.authorize(secretKey, code);
        if (codeIsValid) {
            if (profile.getStaffProfile().isLocked()) {
                profile.getStaffProfile().setLocked(false);
                return true;
            }
        }
        return false;
    }

    public static void handleGrantProcedure(GrantProcedure procedure, Player player, String message) {
        if (procedure.getStage() == GrantProcedure.Stage.REQUIRE_REASON && procedure.getType() == GrantProcedure.Type.REMOVE) {
            if (message.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the grant removal procedure.");
                return;
            }

            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeleteGrantMenu(player, procedure, message));
        }

        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_TIME && procedure.getType() == GrantProcedure.Type.ADD) {
            if (message.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the grant procedure.");
                return;
            }

            Duration duration = Duration.fromString(message);

            if (duration.getValue() == -1) {
                procedure.cancel();
                player.sendMessage(CC.RED + "That duration is not valid. Canceling grant procedure.");
                player.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
            } else {
                Grant grant = procedure.getGrant();
                grant.setDuration(duration.getValue());
                procedure.setGrant(grant);
                procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new GrantReasonMenu(player, procedure.getRecipient(), procedure));
            }
            return;
        }

        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_REASON && procedure.getType() == GrantProcedure.Type.ADD) {
            if (message.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the grant procedure.");
                return;
            }

            Grant grant = procedure.getGrant();
            grant.setAddedReason(message);
            procedure.setGrant(grant);
            procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);
            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new ConfirmGrantMenu(player, procedure.getRecipient(), procedure));
        }
    }

    public static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, String message) {
        if (procedure.getStage() == PunishmentProcedure.Stage.REQUIRE_TEXT) {

            if (message.equalsIgnoreCase("cancel")) {
                PunishmentProcedure.getProcedures().remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the punishment procedure.");
                return;
            }

            if (procedure.getType() == PunishmentProcedure.Type.REMOVE) {
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeletePunishmentMenu(player, procedure, message));
            }
        }
    }
}
