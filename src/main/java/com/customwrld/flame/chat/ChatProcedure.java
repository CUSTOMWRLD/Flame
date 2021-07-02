package com.customwrld.flame.chat;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.menus.grants.ConfirmGrantMenu;
import com.customwrld.flame.menus.grants.DeleteGrantMenu;
import com.customwrld.flame.menus.grants.GrantReasonMenu;
import com.customwrld.flame.menus.punishments.DeletePunishmentMenu;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Duration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatProcedure {

    public static void handleAuthentication(Player player, Profile profile, Component message) {
        TextComponent component = (TextComponent) message;
        String content = component.content();

        try {
            int code = Integer.parseInt(content);

            if (correctCode(profile, code)) {
                player.sendMessage(MC.Style.SEPARATOR);
                player.sendMessage(Component.text("Access Granted! Thank you for Authenticating!")
                        .color(MC.CC.GREEN.getTextColor()));
                player.sendMessage(MC.Style.SEPARATOR);
            } else {
                Bukkit.getScheduler().runTask(Flame.getFlame(), () -> player.kick(Component.text("Incorrect or Expired Two Factor Code")
                        .color(NamedTextColor.RED)));
            }
        } catch (NumberFormatException e) {
            Bukkit.getScheduler().runTask(Flame.getFlame(), () -> player.kick(Component.text("Incorrect or Expired Two Factor Code!")
                    .color(MC.CC.RED.getTextColor())));
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

    public static void handleGrantProcedure(GrantProcedure procedure, Player player, Component message) {
        TextComponent component = (TextComponent) message;
        String content = component.content();

        if (procedure.getStage() == GrantProcedure.Stage.REQUIRE_REASON && procedure.getType() == GrantProcedure.Type.REMOVE) {
            if (content.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(Component.text("You have cancelled the grant removal procedure.")
                        .color(MC.CC.RED.getTextColor()));
                return;
            }

            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeleteGrantMenu(player, procedure, content));
        }

        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_TIME && procedure.getType() == GrantProcedure.Type.ADD) {
            if (content.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(Component.text("You have cancelled the grant procedure.")
                        .color(MC.CC.RED.getTextColor()));
                return;
            }

            Duration duration = Duration.fromString(content);

            if (duration.getValue() == -1) {
                procedure.cancel();
                player.sendMessage(Component.text("That duration is not valid. Canceling grant procedure.")
                        .color(MC.CC.RED.getTextColor()));
                player.sendMessage(Component.text("Example: [perm/1y1m1w1d]")
                        .color(MC.CC.RED.getTextColor()));
            } else {
                Grant grant = procedure.getGrant();
                grant.setDuration(duration.getValue());
                procedure.setGrant(grant);
                procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new GrantReasonMenu(player, procedure));
            }
            return;
        }

        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_REASON && procedure.getType() == GrantProcedure.Type.ADD) {
            if (content.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(Component.text("You have cancelled the grant procedure.")
                        .color(MC.CC.RED.getTextColor()));
                return;
            }

            Grant grant = procedure.getGrant();
            grant.setAddedReason(content);
            procedure.setGrant(grant);
            procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);
            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new ConfirmGrantMenu(player, procedure));
        }
    }

    public static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, Component message) {
        TextComponent component = (TextComponent) message;
        String content = component.content();

        if (procedure.getStage() == PunishmentProcedure.Stage.REQUIRE_TEXT) {

            if (content.equalsIgnoreCase("cancel")) {
                PunishmentProcedure.getProcedures().remove(procedure);
                player.sendMessage(Component.text("You have cancelled the punishment procedure.")
                        .color(MC.CC.RED.getTextColor()));
                return;
            }

            if (procedure.getType() == PunishmentProcedure.Type.REMOVE) {
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeletePunishmentMenu(player, procedure, content));
            }
        }
    }
}
