package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.api.APIProvider;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class APIListener implements APIProvider {

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

        if(rank != null && !rank.api().isDefaultRank()) {
            player.sendMessage(MC.Style.SEPARATOR);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                    Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                            (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                    .color(NamedTextColor.GRAY));
            player.sendMessage(MC.Style.SEPARATOR);
        }

        Util.setupBukkitPlayer(profile);
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

        Util.setupBukkitPlayer(profile);
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

        Util.setupBukkitPlayer(profile);
    }

    public void announcePunishment(Profile profile, Punishment punishment) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    if (player.hasPermission("flame.staff")) {
                        player.sendMessage(MC.Style.SEPARATOR);
                        player.sendMessage(MC.Chat.notificationMessage("Punishment", FlameAPI.getColoredName(profile)
                                .append(Component.text(" has been " + punishment.api().getContext() + " by ")
                                        .color(NamedTextColor.GRAY))
                                .append(punishment.getAddedByUUID() != null ?
                                        FlameAPI.getColoredName(Profile.getProfile(punishment.getAddedByUUID())) :
                                        MC.Style.CONSOLE)));
                        player.sendMessage(MC.Style.SEPARATOR);
                    }
                });
    }

    @Override
    public void punishmentAdd(Punishment punishment) {
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);

        Player player = Bukkit.getPlayer(profile.getId());

        if(player != null && !punishment.isRemoved()) {
            if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
                player.kick(Component.join(Component.newline(), Util.getPunishmentMessage(punishment)));
            } else if (punishment.getType() == Punishment.Type.MUTE) {
                Util.getPunishmentMessage(punishment).forEach(player::sendMessage);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 0.1F);
            }
        }
    }

    @Override
    public void punishmentExpire(Punishment punishment) {
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);
    }

    @Override
    public void punishmentRemove(Punishment punishment) {
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);
    }

}
