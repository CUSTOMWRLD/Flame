package com.customwrld.flame.event;

import com.customwrld.commonlib.modules.bukkit.BukkitAPI;
import com.customwrld.commonlib.modules.bukkit.events.GrantAddEvent;
import com.customwrld.commonlib.modules.bukkit.events.GrantExpireEvent;
import com.customwrld.commonlib.modules.bukkit.events.GrantRemoveEvent;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.commonlib.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GrantListener implements Listener {

    @EventHandler
    public void onGrantAdd(GrantAddEvent event) {
        Grant grant = event.getGrant();
        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Player player = BukkitAPI.getPlayer(profile);
        Rank rank = grant.getApi().getRank();

        if(player != null && rank != null) {
            player.sendMessage(Minecraft.Style.SEPARATOR);
            player.sendMessage(Minecraft.Style.getMessageComponent("Grants",
                    "A '" + rank.getName() + "' grant has been applied to you " +
                    (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."));
            player.sendMessage(Minecraft.Style.SEPARATOR);
        }
    }

    @EventHandler
    public void onGrantRemove(GrantRemoveEvent event) {
        Grant grant = event.getGrant();
        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Player player = BukkitAPI.getPlayer(profile);
        Rank rank = grant.getApi().getRank();

        if(player != null && rank != null) {
            player.sendMessage(Minecraft.Style.SEPARATOR);
            player.sendMessage(Minecraft.Style.getMessageComponent("Grants", "Your '" + rank.getName() + "' grant has been removed."));
            player.sendMessage(Minecraft.Style.SEPARATOR);
        }
    }

    @EventHandler
    public void onGrantExpire(GrantExpireEvent event) {
        Grant grant = event.getGrant();
        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Player player = BukkitAPI.getPlayer(profile);
        Rank rank = grant.getApi().getRank();

        if(player != null && rank != null) {
            player.sendMessage(Minecraft.Style.SEPARATOR);
            player.sendMessage(Minecraft.Style.getMessageComponent("Grants", "Your '" + rank.getName() + "' grant has expired."));
            player.sendMessage(Minecraft.Style.SEPARATOR);
        }
    }

}
