package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.api.GrantProvider;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.design.Tab;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GrantListener implements GrantProvider {

    @Override
    public void grantAdd(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());

        if(player == null) {
            return;
        }

        Tab.updateTeam(player);

        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Rank rank = grant.api().getRank();

        if(rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                            (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                    .color(NamedTextColor.GRAY));
            player.sendMessage(MC.Style.SEPARATOR);
        }
    }

    @Override
    public void grantExpire(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());

        if(player == null) {
            return;
        }

        Tab.updateTeam(player);

        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Rank rank = grant.api().getRank();

        if(rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("Your '" + rank.getName() + "' grant has expired.")
                            .color(NamedTextColor.GRAY)));
            player.sendMessage(MC.Style.SEPARATOR);
        }
    }

    @Override
    public void grantRemove(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());

        if(player == null) {
            return;
        }

        Tab.updateTeam(player);

        Profile profile = ProfileAPI.getCachedProfile(grant.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        Rank rank = grant.api().getRank();

        if(rank != null) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("Your '" + rank.getName() + "' grant has been removed.")
                            .color(NamedTextColor.GRAY)));
            player.sendMessage(MC.Style.SEPARATOR);
        }
    }
}
