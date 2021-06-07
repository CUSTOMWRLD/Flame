package com.customwrld.flame.chat;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.menus.grants.ConfirmGrantMenu;
import com.customwrld.flame.menus.grants.DeleteGrantMenu;
import com.customwrld.flame.menus.grants.GrantReasonMenu;
import com.customwrld.flame.menus.punishments.DeletePunishmentMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureStage;
import com.customwrld.flame.profile.grant.procedure.GrantProcedureType;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedureStage;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedureType;
import com.customwrld.flame.util.Duration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatProcedure {

//    @SneakyThrows
//    public static void handleAuthentication(Player player, Profile profile, String message) {
//        int code = Integer.parseInt(message);
//        JDA jda = CustomLib.getCustomLib().getJda();
//
//        Guild guild = jda.getGuildById(Config.DISCORD_GUILD.format());
//
//        if (guild != null) {
//            TextChannel channel = guild.getTextChannelById(Config.DISCORD_NOTIFICATIONS.format());
//
//            if (channel != null) {
//                if (correctCode(profile, code)) {
//                    EmbedBuilder builder = new EmbedBuilder();
//
//                    builder.setColor(Color.GREEN)
//                            .addField("**2FA Code Correct**", player.getName() + " has correctly entered their 2FA Code.", false);
//                    channel.sendMessage(builder.build()).queue();
//
//                    player.sendMessage(CC.CHAT_BAR);
//                    player.sendMessage(CC.GREEN + "Access Granted! Thank you for Authenticating!");
//                    player.sendMessage(CC.CHAT_BAR);
//                } else {
//                    EmbedBuilder builder = new EmbedBuilder();
//
//                    channel.sendMessage("> <@&" + Flame.getFlame().getMainConfig().getString("DISCORD.SYNC.RANKS.OWNER.ROLEID") + ">").queue();
//                    builder.setColor(Color.RED)
//                            .addField("**2FA Code Incorrect**", player.getName() + " has incorrectly entered their 2FA Code.", false);
//                    channel.sendMessage(builder.build()).queue();
//
//                    Bukkit.getScheduler().runTask(Flame.getFlame(), () -> player.kickPlayer(CC.RED + "Incorrect or expired 2FA code!"));
//                }
//            }
//        }
//    }

    private static boolean correctCode(Profile profile, int code) {
        String secretKey = profile.getStaffProfile().authKey();
        GoogleAuthenticator auth = Flame.getFlame().getAuth();
        boolean codeIsValid = auth.authorize(secretKey, code);
        if (codeIsValid) {
            if (profile.getStaffProfile().locked()) {
                profile.getStaffProfile().locked(false);
                return true;
            }
        }
        return false;
    }

    public static void handleGrantProcedure(GrantProcedure procedure, Player player, String message) {
        if (procedure.getStage() == GrantProcedureStage.REQUIRE_REASON && procedure.getType() == GrantProcedureType.REMOVE) {
            if (message.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the grant removal procedure.");
                return;
            }

            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeleteGrantMenu(player, procedure, message));
        }

        if (procedure.getStage() == GrantProcedureStage.PROVIDE_CUSTOM_TIME && procedure.getType() == GrantProcedureType.GRANT) {
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
                procedure.setStage(GrantProcedureStage.REQUIRE_REASON);
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new GrantReasonMenu(player, procedure.getRecipient(), procedure));
            }
            return;
        }

        if (procedure.getStage() == GrantProcedureStage.PROVIDE_CUSTOM_REASON && procedure.getType() == GrantProcedureType.GRANT) {
            if (message.equalsIgnoreCase("cancel")) {
                GrantProcedure.procedures.remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the grant procedure.");
                return;
            }

            Grant grant = procedure.getGrant();
            grant.setAddedReason(message);
            procedure.setGrant(grant);
            procedure.setStage(GrantProcedureStage.REQUIRE_CONFIRMATION);
            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new ConfirmGrantMenu(player, procedure.getRecipient(), procedure));
        }
    }

    public static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, String message) {
        if (procedure.getStage() == PunishmentProcedureStage.REQUIRE_TEXT) {

            if (message.equalsIgnoreCase("cancel")) {
                PunishmentProcedure.getProcedures().remove(procedure);
                player.sendMessage(CC.RED + "You have cancelled the punishment procedure.");
                return;
            }

            if (procedure.getType() == PunishmentProcedureType.PARDON) {
                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeletePunishmentMenu(player, procedure, message));
            }
        }
    }
}
