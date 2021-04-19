package com.customwrld.flame.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;

public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        event.setCancelled(true);

        if (profile == null || !profile.isLoaded()) {
            player.sendMessage(Config.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        if (profile.getStaffProfile().locked()) {
            ChatProcedure.handleAuthentication(player, profile, message);
            return;
        }

        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(player);

        if (punishmentProcedure != null) {
            ChatProcedure.handlePunishmentProcedure(punishmentProcedure, player, message);
            return;
        }

        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(player);

        if (grantProcedure != null) {
            ChatProcedure.handleGrantProcedure(grantProcedure, player, message);
            return;
        }

        if (!player.hasPermission("flame.bypasschat")) {
            Punishment muted = profile.getActivePunishmentByType(PunishmentType.MUTE);

            if (Flame.get().isChatMuted()) {
                player.sendMessage(CC.RED + "Chat is currently muted.");
                return;
            }

            if (muted != null) {
                if (muted.isPermanent()) {
                    player.sendMessage(CC.RED + "You are permanently muted.");
                } else {
                    player.sendMessage(CC.RED + "You are muted for another " + muted.getTimeRemaining() + ".");
                }
                player.sendMessage(CC.RED + "Reason: " + muted.getAddedReason());
                return;
            }

            if (!ChatFilter.isSafe(message)) {
                Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                    if (onlinePlayer.hasPermission("flame.staff")) {
                        onlinePlayer.sendMessage(CC.DARK_GRAY + "[" + CC.RED + "\u27a4" + CC.DARK_GRAY + "] " + formatChat(profile, message));
                    }
                });
                player.sendMessage(formatChat(profile, message));
                return;
            }
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            Profile playerProfile = Profile.getByUuid(onlinePlayer.getUniqueId());

            if (playerProfile.getOptions().publicChatEnabled()) {
                onlinePlayer.sendMessage(formatChat(profile, message));
            }
        });
    }

    public String formatChat(Profile profile, String message) {
        return CC.translate(FlameAPI.getChatFormat(profile)) + CC.GRAY + ": " + ChatColor.of(bleach(profile.getActiveRank().getColor().getColor(), 0.85)) + message;
    }

    public Color bleach(Color color, double amount) {
        int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
        int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
        int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
        return new Color(red, green, blue);
    }
}
