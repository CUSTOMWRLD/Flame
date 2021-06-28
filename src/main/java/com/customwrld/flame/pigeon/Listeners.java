package com.customwrld.flame.pigeon;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.pigeon.payloads.MessagePlayerPayload;
import com.customwrld.flame.pigeon.payloads.conversation.ConversationFromPayload;
import com.customwrld.flame.pigeon.payloads.conversation.ConversationToPayload;
import com.customwrld.flame.pigeon.payloads.proxy.StaffConnectPayload;
import com.customwrld.flame.pigeon.payloads.proxy.StaffDisconnectPayload;
import com.customwrld.flame.pigeon.payloads.proxy.StaffSwitchPayload;
import com.customwrld.flame.pigeon.payloads.rank.ReloadRankPayload;
import com.customwrld.flame.pigeon.payloads.rank.RemoveRankPayload;
import com.customwrld.flame.pigeon.payloads.server.ServerOfflinePayload;
import com.customwrld.flame.pigeon.payloads.server.ServerOnlinePayload;
import com.customwrld.pigeon.annotations.PayloadHandler;
import com.customwrld.pigeon.annotations.PayloadListener;
import com.customwrld.pigeon.listeners.Listener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onServerOnline(ServerOnlinePayload payload) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("flame.staff"))
                .forEach(player -> player.sendMessage(MC.Chat.notificationMessage("Proxy",
                        Component.text()
                                .append(Component.text(payload.getName() + " has came ", MC.CC.GRAY.getTextColor()))
                                .append(Component.text("Online", MC.CC.GREEN.getTextColor()))
                                .build())));
    }

    @PayloadHandler
    public void onServerOffline(ServerOfflinePayload payload) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("flame.staff"))
                .forEach(player -> player.sendMessage(MC.Chat.notificationMessage("Proxy",
                        Component.text()
                                .append(Component.text(payload.getName() + " has went ", MC.CC.GRAY.getTextColor()))
                                .append(Component.text("Offline", MC.CC.RED.getTextColor()))
                                .build())));
    }

    @PayloadHandler
    public void onReloadRank(ReloadRankPayload payload) {
        Rank rank = Rank.getRank(payload.getRank(), false);

        Rank.getRanks().remove(payload.getRank());

        if (rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);

            FlameAPI.refreshPlayers(rank.getUuid());
        }
    }

    @PayloadHandler
    public void onRemoveRank(RemoveRankPayload payload) {
        UUID rank = payload.getRank();

        Rank.getRanks().remove(rank);

        FlameAPI.refreshPlayers(rank);
    }

    @PayloadHandler
    public void onStaffConnect(StaffConnectPayload payload) {
        Profile staff = Profile.getProfile(payload.getUuid());

        if (staff != null) {
            Bukkit.getScheduler().runTaskLater(Flame.getFlame(), () -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("flame.staff"))
                    .forEach(player -> player.sendMessage(MC.Chat.notificationMessage("Proxy",
                            Component.text()
                                    .append(FlameAPI.getColoredName(staff))
                                    .append(Component.text(" has connected to " + payload.getServer() + ".")
                                            .color(MC.CC.GRAY.getTextColor()))
                                    .build()))), 20L);
        }
    }

    @PayloadHandler
    public void onStaffDisconnect(StaffDisconnectPayload payload) {
        Profile staff = Profile.getProfile(payload.getUuid());

        if (staff != null) {
            Bukkit.getScheduler().runTaskLater(Flame.getFlame(), () -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("flame.staff"))
                    .forEach(player -> player.sendMessage(MC.Chat.notificationMessage("Proxy",
                            Component.text()
                                    .append(FlameAPI.getColoredName(staff))
                                    .append(Component.text(" has disconnected from " + payload.getServer() + ".")
                                            .color(MC.CC.GRAY.getTextColor()))
                                    .build()))), 20L);
        }

    }

    @PayloadHandler
    public void onStaffSwitch(StaffSwitchPayload payload) {
        Profile staff = Profile.getProfile(payload.getUuid());

        if (staff != null) {
            Bukkit.getScheduler().runTaskLater(Flame.getFlame(), () -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("flame.staff"))
                    .forEach(player -> player.sendMessage(MC.Chat.notificationMessage("Proxy",
                            Component.text()
                                    .append(FlameAPI.getColoredName(staff))
                                    .append(Component.text(" has switched to " + payload.getServerTo() + " from " + payload.getServerFrom() + ".")
                                            .color(MC.CC.GRAY.getTextColor()))
                                    .build()))), 20L);
        }

    }

    @PayloadHandler
    public void onConversationTo(ConversationToPayload payload) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getUniqueId().equals(payload.getInitiator()))
                .forEach(player -> player.sendMessage(Component.text()
                        .append(Component.text("(To ")
                                .color(MC.CC.GRAY.getTextColor()))
                        .append(FlameAPI.getColoredName(Profile.getProfile(payload.getTarget())))
                        .append(Component.text(") " + payload.getMessage())
                                .color(MC.CC.GRAY.getTextColor()))
                        .build()));
    }

    @PayloadHandler
    public void onConversationFrom(ConversationFromPayload payload) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getUniqueId().equals(payload.getTarget()))
                .forEach(player -> {
                    player.sendMessage(Component.text()
                            .append(Component.text("(From ")
                                    .color(MC.CC.GRAY.getTextColor()))
                            .append(FlameAPI.getColoredName(Profile.getProfile(payload.getInitiator())))
                            .append(Component.text(") " + payload.getMessage())
                                    .color(MC.CC.GRAY.getTextColor()))
                            .build());
                    Profile profile = Profile.getProfile(payload.getTarget());

                    if (profile != null) {
                        if (profile.getOptionsProfile().isReceivingMessageSounds()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 0F);
                        }
                    }
                });
    }

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(payload.getPlayer())) {
                player.sendMessage(MC.Style.translateLegacy(payload.getMessage()));
                return;
            }
        }
    }

}
