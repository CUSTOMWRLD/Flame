package com.customwrld.flame.profile.conversation;

import com.customwrld.flame.profile.Profile;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ProfileConversations {

    @Getter
    private final Profile profile;
    @Getter
    private final Map<UUID, Conversation> conversations;

    public ProfileConversations(Profile profile) {
        this.profile = profile;
        this.conversations = new HashMap<>();
    }

    public boolean canBeMessagedBy(Player player) {
        if (profile.isIgnoring(player)) {
            return false;
        }

        return profile.getOptions().conversationsEnabled();
    }

    public Conversation getOrCreateConversation(Player target) {
        Player sender = profile.getPlayer();

        if (sender != null) {
            Conversation conversation = conversations.get(target.getUniqueId());

            if (conversation == null) {
                conversation = new Conversation(profile.getUuid(), target.getUniqueId());
            }

            return conversation;
        }

        return null;
    }

    public Conversation getLastRepliedConversation() {
        List<Conversation> list = conversations
                .values()
                .stream()
                .sorted(Comparator.comparingLong(Conversation::getLastMessageSentAt))
                .collect(Collectors.toList());

        Collections.reverse(list);

        return list.isEmpty() ? null : list.get(0);
    }

    public void expireAllConversations() {
        this.conversations.clear();
    }

}