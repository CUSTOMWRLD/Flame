package com.customwrld.flame.event;

import com.customwrld.commonlib.modules.bukkit.BukkitAPI;
import com.customwrld.commonlib.modules.bukkit.events.PunishmentAddEvent;
import com.customwrld.commonlib.modules.bukkit.events.PunishmentExpireEvent;
import com.customwrld.commonlib.modules.bukkit.events.PunishmentRemoveEvent;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.profile.ProfileAPI;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.FlameAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PunishmentListener implements Listener {

    public void announcePunishment(Profile profile, Punishment punishment) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    if (player.hasPermission("flame.staff")) {
                        player.sendMessage(Minecraft.Style.SEPARATOR);
                        player.sendMessage(Minecraft.Style.getMessageComponent("Punishment", FlameAPI.getColoredName(profile)
                                .append(Component.text(" has been " + punishment.getApi().getContext() + " by ")
                                        .color(NamedTextColor.GRAY))
                                .append(punishment.getAddedByUUID() != null ?
                                        FlameAPI.getColoredName(Profile.getProfile(null, punishment.getAddedByUUID())) :
                                        Minecraft.Style.CONSOLE)));
                        player.sendMessage(Minecraft.Style.SEPARATOR);
                    }
                });
    }

    @EventHandler
    public void onPunishmentAdd(PunishmentAddEvent event) {
        Punishment punishment = event.getPunishment();
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);

        Player player = BukkitAPI.getPlayer(profile);

        if(player != null && !punishment.isRemoved()) {
            if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
                player.kick(Component.join(Component.newline(), punishment.getApi().getPunishmentMessage()), PlayerKickEvent.Cause.BANNED);
            } else if (punishment.getType() == Punishment.Type.MUTE) {
                punishment.getApi().getPunishmentMessage().forEach(player::sendMessage);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 0.1F);
            }
        }
    }

    @EventHandler
    public void onPunishmentRemove(PunishmentRemoveEvent event) {
        Punishment punishment = event.getPunishment();
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);
    }

    @EventHandler
    public void onPunishmentExpire(PunishmentExpireEvent event) {
        Punishment punishment = event.getPunishment();
        Profile profile = ProfileAPI.getCachedProfile(punishment.getPlayerUUID());

        if (profile == null) {
            return;
        }

        profile.load();

        announcePunishment(profile, punishment);
    }

}
