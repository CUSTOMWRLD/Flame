package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.api.PunishmentProvider;
import com.customwrld.commonlib.modules.bukkit.BukkitAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PunishmentListener implements PunishmentProvider {

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
