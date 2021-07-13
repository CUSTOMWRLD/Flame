package com.customwrld.flame.pigeon;

import com.customwrld.commonlib.modules.pigeon.payloads.conversation.ConversationFromPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.conversation.ConversationToPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.minecraft.MessagePlayerPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.network.ServerOfflinePayload;
import com.customwrld.commonlib.modules.pigeon.payloads.network.ServerOnlinePayload;
import com.customwrld.commonlib.modules.pigeon.payloads.rank.RankReloadPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.rank.RankRemovePayload;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.listeners.ChatListener;
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
    public void onReloadRank(RankReloadPayload payload) {
        Rank rank = Rank.getRank(payload.getRank(), false);

        Rank.getRanks().remove(payload.getRank());

        if (rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);

            FlameAPI.refreshPlayers(rank.getUuid());
        }
    }

    @PayloadHandler
    public void onRemoveRank(RankRemovePayload payload) {
        UUID rank = payload.getRank();

        Rank.getRanks().remove(rank);

        FlameAPI.refreshPlayers(rank);
    }

    @PayloadHandler
    public void onConversationTo(ConversationToPayload payload) {
        var initiator = Bukkit.getPlayer(payload.getInitiator());
        if(initiator == null || !initiator.isOnline()) { return; }

        Profile.getProfile(payload.getTarget()).thenAccept(targetProfile -> {
            if(targetProfile == null) { return; }
            initiator.sendMessage(
                    Component.text("(To ", MC.CC.GRAY.getTextColor())
                            .append(ChatListener.hoverableFormat(targetProfile))
                            .append(Component.text(") " + payload.getMessage(), MC.CC.GRAY.getTextColor())));
        });
    }

    @PayloadHandler
    public void onConversationFrom(ConversationFromPayload payload) {
        var target = Bukkit.getPlayer(payload.getTarget());
        if(target == null || !target.isOnline()) { return; }

        Profile.getProfile(payload.getInitiator())
                .thenAccept(initiatorProfile -> {
                    if(initiatorProfile == null) { return; }
                    target.sendMessage(Component.text()
                            .append(Component.text("(From ")
                                    .color(MC.CC.GRAY.getTextColor()))
                            .append(ChatListener.hoverableFormat(initiatorProfile))
                            .append(Component.text(") " + payload.getMessage())
                                    .color(MC.CC.GRAY.getTextColor()))
                            .build());

                    ProfileUtil.getAssociatedProfile(target)
                            .thenAccept(targetProfile -> {
                                if(targetProfile.getOptionsProfile().isReceivingMessageSounds())
                                    target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 0F);
                            });
                });
    }

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(payload.getPlayer())) {
                player.sendMessage(payload.getMessage());
                return;
            }
        }
    }

}
