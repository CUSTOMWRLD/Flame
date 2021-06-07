package com.customwrld.flame.profile.conversation;

import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Conversation {

    @Getter private final UUID initiatedBy;
    @Getter private final UUID target;
    @Getter private long lastMessageSentAt;
    @Getter private UUID lastMessageSentBy;

    public Conversation(UUID initiatedBy, UUID target) {
        this.initiatedBy = initiatedBy;
        this.target = target;
        this.lastMessageSentAt = System.currentTimeMillis();

        Profile initiatorProfile = Profile.getByUuid(initiatedBy);
        initiatorProfile.getConversations().getConversations().put(target, this);

        Profile targetProfile = Profile.getByUuid(target);
        targetProfile.getConversations().getConversations().put(initiatedBy, this);
    }

    public void sendMessage(Player sender, Player target, String message) {
        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile == null || !senderProfile.isLoaded() || targetProfile == null || !targetProfile.isLoaded()) {
            sender.sendMessage(Config.COULD_NOT_RESOLVE_PLAYER.format());
            return;
        }

        sender.sendMessage(Config.CONVERSATION_SEND_MESSAGE.format(FlameAPI.getColoredName(targetProfile), message));

        if (targetProfile.getOptions().soundsEnabled()) {
            target.playSound(target.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
        }

        target.sendMessage(Config.CONVERSATION_RECEIVE_MESSAGE.format(FlameAPI.getColoredName(senderProfile), message));

        lastMessageSentAt = System.currentTimeMillis();
        lastMessageSentBy = sender.getUniqueId();
    }

    public boolean validate() {
        Player initiator = Bukkit.getPlayer(initiatedBy);

        if (initiator == null || !initiator.isOnline()) {
            destroy();
            return false;
        }

        Player target = Bukkit.getPlayer(this.target);

        if (target == null || !target.isOnline()) {
            destroy();
            return false;
        }

        return true;
    }

    public void destroy() {
        for (Player player : new Player[]{Bukkit.getPlayer(initiatedBy), Bukkit.getPlayer(target)}) {
            if (player != null && player.isOnline()) {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.getConversations().getConversations().remove(getPartner(player.getUniqueId()));
            }
        }
    }

    public UUID getPartner(UUID compareWith) {
        if (initiatedBy.equals(compareWith)) {
            return target;
        } else if (target.equals(compareWith)) {
            return initiatedBy;
        }

        return null;
    }
}