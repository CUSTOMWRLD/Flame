package com.customwrld.flame.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.grant.Grant;Procedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncChatEvent event) {
        String message = event.getMessage();
        Player sender = event.getPlayer();
        Profile senderProfile = Profile.getProfiles().get(sender.getUniqueId());

        event.setCancelled(true);

        if (senderProfile == null || !senderProfile.isLoaded()) {
            sender.sendMessage(Config.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        if (senderProfile.getStaffProfile().isLocked()) {
            ChatProcedure.handleAuthentication(sender, senderProfile, message);
            return;
        }

        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(sender);

        if (punishmentProcedure != null) {
            ChatProcedure.handlePunishmentProcedure(punishmentProcedure, sender, message);
            return;
        }

        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(sender);

        if (grantProcedure != null) {
            ChatProcedure.handleGrantProcedure(grantProcedure, sender, message);
            return;
        }

        if (!sender.hasPermission("flame.staff")) {
            Punishment punishment = senderProfile.getActivePunishmentByType(Punishment.Type.MUTE);

            if (FlameAPI.isChatMuted()) {
                sender.sendMessage(CC.CHAT_BAR);
                sender.sendMessage(CC.RED + "The chat is currently muted! Please try again later.");
                sender.sendMessage(CC.CHAT_BAR);
                return;
            }

            if (punishment != null) {
                punishment.getPunishmentMessage().forEach(sender::sendMessage);
                return;
            }

            if (!ChatFilter.isSafe(message)) {
                Bukkit.getServer().getOnlinePlayers().stream()
                        .filter(staff -> staff.hasPermission("flame.staff"))
                        .forEach(staff -> staff.sendMessage(CC.DARK_GRAY + "[" + CC.RED + "➤" + CC.DARK_GRAY + "] " + formatChat(senderProfile, message)));

                sender.sendMessage(formatChat(senderProfile, message));
                return;
            }
        }

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(player -> Profile.getProfiles().get(player.getUniqueId()).getOptions().isReceivingPublicChat())
                .forEach(player -> player.sendMessage(formatChat(senderProfile, message)));
    }

    public String formatChat(Profile profile, String message) {
        return CC.translate(FlameAPI.getChatFormat(profile)) + CC.GRAY + ": " + ChatColor.of(bleach(profile.getActiveRank().getColor(), 0.85)) + message;
    }

    public Color bleach(Color color, double amount) {
        int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
        int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
        int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
        return new Color(red, green, blue);
    }
}
