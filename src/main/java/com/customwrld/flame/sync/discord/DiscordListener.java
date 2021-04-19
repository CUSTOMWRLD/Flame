package com.customwrld.flame.sync.discord;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.sync.AccountSync;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        DiscordSync discordSync = AccountSync.getDiscordSync();
        if (event.getTextChannel().equals(discordSync.channel)) {
            for (Map.Entry<Profile, SyncCode> ent : discordSync.syncCodes.entrySet()) {
                if (event.getMessage().getContentRaw().equals(ent.getValue().getCode())) {
                    discordSync.submitSyncCode(ent.getKey(), event.getMember());
                }
            }
        }
    }
}
