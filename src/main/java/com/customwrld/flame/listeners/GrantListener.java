package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.bukkit.BukkitAPI;
import com.customwrld.commonlib.modules.bukkit.events.GrantAddEvent;
import com.customwrld.commonlib.modules.bukkit.events.GrantExpireEvent;
import com.customwrld.commonlib.modules.bukkit.events.GrantRemoveEvent;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        Rank rank = grant.api().getRank();

        if(player != null && rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                            (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                    .color(NamedTextColor.GRAY));
            player.sendMessage(MC.Style.SEPARATOR);
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
        Rank rank = grant.api().getRank();

        if(player != null && rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("Your '" + rank.getName() + "' grant has been removed.")
                            .color(NamedTextColor.GRAY)));
            player.sendMessage(MC.Style.SEPARATOR);
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
        Rank rank = grant.api().getRank();

        if(player != null && rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("Your '" + rank.getName() + "' grant has expired.")
                            .color(NamedTextColor.GRAY)));
            player.sendMessage(MC.Style.SEPARATOR);
        }
    }

}
