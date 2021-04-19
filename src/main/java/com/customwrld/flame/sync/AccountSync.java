package com.customwrld.flame.sync;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.sync.discord.DiscordSync;
import lombok.Getter;

public class AccountSync {
    @Getter
    private static DiscordSync discordSync;

    public static void init() {
        discordSync = new DiscordSync();

        if (CustomLib.getInstance().getJda() != null) {
            discordSync.beginTask();
        } else {
            new RuntimeException("Discord bot wasn't able to start.").printStackTrace();
        }
    }

    public static void resynchronize(Profile profile) {
        if (profile.isSyncedWithDiscord()) {
            discordSync.resync(profile);
        }
    }

    public static void addGrant(Profile profile, Grant grant) {
        if (profile.isSyncedWithDiscord()) {
            discordSync.addSyncRole(profile, grant.getRank());
        }
    }

    public static void removeGrant(Profile profile, Grant grant) {
        if (profile.isSyncedWithDiscord()) {
            discordSync.removeSyncRole(profile, grant.getRank());
        }
    }
}
