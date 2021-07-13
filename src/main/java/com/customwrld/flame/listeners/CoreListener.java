package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.api.APIListener;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CoreListener implements APIListener {

    @Override
    public void grantAdd(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());
        if(player == null) { return; }

        Tab.updateTeam(player);

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(profile == null) { return; }

            if(rank != null && !rank.api().isDefaultRank()) {
                player.sendMessage(MC.Style.SEPARATOR);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                                (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                        .color(NamedTextColor.GRAY));
                player.sendMessage(MC.Style.SEPARATOR);

                System.out.println(profile.api().getAllPermissions());
                System.out.println(rank.api().getAllPermissions(true));
            }

            Util.setupBukkitPlayer(profile);
        });
    }

    @Override
    public void grantExpire(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());
        if(player == null) { return; }

        Tab.updateTeam(player);

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(rank != null) {
                player.sendMessage(MC.Style.SEPARATOR);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("Your '" + rank.getName() + "' grant has expired.")
                                .color(NamedTextColor.GRAY)));
                player.sendMessage(MC.Style.SEPARATOR);
            }

            Util.setupBukkitPlayer(profile);
        });
    }

    @Override
    public void grantRemove(Grant grant) {
        Player player = Bukkit.getPlayer(grant.getPlayerUUID());
        if(player == null) { return; }

        Tab.updateTeam(player);

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(rank != null) {
                player.sendMessage(MC.Style.SEPARATOR);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("Your '" + rank.getName() + "' grant has been removed.")
                                .color(NamedTextColor.GRAY)));
                player.sendMessage(MC.Style.SEPARATOR);
            }

            Util.setupBukkitPlayer(profile);
        });
    }

    public void announcePunishment(Profile receiverProfile, Punishment punishment) {
        Profile.getProfile(punishment.getAddedByUUID()).thenAccept(profile -> {
            Component component = Component.text()
                    .append(MC.Style.SEPARATOR)
                    .append(MC.Chat.notificationMessage("Punishment", receiverProfile.api().getColoredName()
                            .append(Component.text(" has been " + punishment.api().getContext() + " by ")
                                    .color(NamedTextColor.GRAY))
                            .append(punishment.getAddedByUUID() != null ?
                                    ChatListener.hoverableFormat(profile) :
                                    MC.Style.CONSOLE)))
                    .append(MC.Style.SEPARATOR)
                    .build();

            Bukkit.getOnlinePlayers().forEach(player -> {
                if(!player.hasPermission("flame.staff")) { return; }
                player.sendMessage(component);
            });
        });

    }

    @Override
    public void punishmentAdd(Punishment punishment) {
        Player player = Bukkit.getPlayer(punishment.getPlayerUUID());
        if(player == null) { return; }

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            announcePunishment(profile, punishment);

            if(!punishment.isRemoved()) {
                if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
                    player.kick(Component.join(Component.newline(), Util.getPunishmentMessage(punishment)));
                } else if (punishment.getType() == Punishment.Type.MUTE) {
                    Util.getPunishmentMessage(punishment).forEach(player::sendMessage);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 0.1F);
                }
            }
        });
    }

    @Override
    public void punishmentExpire(Punishment punishment) {
        ProfileUtil.getProfile(punishment.getPlayerUUID()).thenAccept(profile -> {
            if(profile == null) { return; }
            announcePunishment(profile, punishment);
        });
    }

    @Override
    public void punishmentRemove(Punishment punishment) {
        ProfileUtil.getProfile(punishment.getPlayerUUID()).thenAccept(profile -> {
            if (profile == null) { return; }
            announcePunishment(profile, punishment);
        });
    }

}
