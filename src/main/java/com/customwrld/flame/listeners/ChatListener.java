package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.chat.ChatFilter;
import com.customwrld.flame.chat.ChatProcedure;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Util;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncChatEvent event) {
        Component message = event.message();
        Player sender = event.getPlayer();

        event.setCancelled(true);

        ProfileUtil.getAssociatedProfile(sender)
                .thenAccept(senderProfile -> {
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
                        Punishment punishment = senderProfile.api().getActivePunishmentByType(Punishment.Type.MUTE);

                        if (FlameAPI.isChatMuted()) {
                            sender.sendMessage(MC.Style.SEPARATOR);
                            sender.sendMessage(Component.text("The chat is currently muted! Please try again later.")
                                    .color(MC.CC.RED.getTextColor()));
                            sender.sendMessage(MC.Style.SEPARATOR);
                            return;
                        }

                        if (punishment != null) {
                            Util.getPunishmentMessage(punishment).forEach(sender::sendMessage);
                            return;
                        }

                        if (!ChatFilter.isSafe(message)) {
                            var markedMessage = Component.text("[")
                                    .color(NamedTextColor.DARK_GRAY)
                                    .append(Component.text("➤")
                                            .color(NamedTextColor.RED))
                                    .append(Component.text("] ")
                                            .color(NamedTextColor.DARK_GRAY))
                                    .append(formatChat(senderProfile, message));

                            Bukkit.getServer().getOnlinePlayers().stream()
                                    .filter(staff -> staff.hasPermission("flame.staff"))
                                    .forEach(staff -> {
                                        staff.sendMessage(markedMessage);
                                    });

                            sender.sendMessage(formatChat(senderProfile, message));
                            return;
                        }
                    }

                    Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                        ProfileUtil.getAssociatedProfile(onlinePlayer)
                                .thenAccept(profile -> {
                                    if(profile.getOptionsProfile().isReceivingPublicChat())
                                        onlinePlayer.sendMessage(formatChat(senderProfile, message));
                                });
                    });
                });
    }

    public static TextComponent hoverableFormat(Profile profile) {
        Component chatFormat = profile.api().getChatFormat();
        Rank rank = profile.api().getActiveGrant().api().getRank();
        Color color = rank.api().getRankColor();

        return Component.text()
                .append(chatFormat
                        .hoverEvent(HoverEvent.showText(Component.text()
                                .append(chatFormat
                                        .append(Component.newline()))
                                .append(Component.text()
                                .append(Component.text(" * ")
                                        .color(NamedTextColor.AQUA))
                                .append(Component.text("Name: ")
                                        .color(NamedTextColor.GRAY))
                                .append(FlameAPI.getColoredName(profile))
                                .append(Component.newline()))
                                .append(Component.text()
                                        .append(Component.text(" * ")
                                                .color(NamedTextColor.AQUA))
                                        .append(Component.text("Rank: ")
                                                .color(NamedTextColor.GRAY))
                                        .append(Component.text(rank.getName())
                                                .color(TextColor.color(color.getRGB()))
                                        )
                                )
                        ))
                )
                .build();
    }

    public static TextComponent formatChat(Profile profile, Component message) {
        Rank rank = profile.api().getActiveGrant().api().getRank();
        Color color = rank.api().getRankColor();

        return Component.text()
                .append(hoverableFormat(profile))
                .append(Component.text(" » ")
                        .color(NamedTextColor.DARK_GRAY))
                .append(message.color(TextColor.color(bleach(color, 0.80))))
                .build();
    }

    public static int bleach(Color color, double amount) {
        int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
        int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
        int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
        return new Color(red, green, blue).getRGB();
    }
}