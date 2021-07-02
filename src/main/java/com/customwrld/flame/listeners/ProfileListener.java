package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.bukkit.BukkitAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.customlib.assemble.Assemble;
import com.customwrld.customlib.util.NMSUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.util.Util;
import com.google.common.hash.Hashing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ProfileListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Flame flame = Flame.getFlame();
        Player player = Bukkit.getPlayer(event.getUniqueId());

        if (FlameAPI.isStartingUp()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("Server is currently starting up. Please join back soon.")
                            .color(NamedTextColor.RED));
            return;
        }

        if (player != null && player.isOnline()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("You tried to login too quickly after disconnecting.")
                            .color(NamedTextColor.RED)
                            .append(Component.newline())
                            .append(Component.text("Please try again in a few seconds.")
                                    .color(NamedTextColor.RED)));

            flame.getServer().getScheduler().runTask(flame, () -> player.kick(Component.text("You logged in from another location!")
                    .color(NamedTextColor.RED)));
            return;
        }

        boolean isElevated = flame.getElevatedStaff().contains(event.getUniqueId());

        Profile profile = Profile.getProfile(event.getName(), event.getUniqueId());

        Punishment punishment = profile.api().getActiveBan();

        if (punishment != null && !isElevated) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.join(Component.newline(), Util.getPunishmentMessage(punishment)));
            return;
        }

        profile.setName(event.getName());
        profile.setSearchableName(event.getName());

        if (profile.getFirstSeen() == null) {
            profile.setFirstSeen(System.currentTimeMillis());
        }

        profile.setLastSeen(System.currentTimeMillis());

        String hashedIP = Hashing.sha1().hashString(event.getAddress().getHostAddress(), StandardCharsets.UTF_8).toString();

        if (profile.getCurrentAddress() == null) {
            profile.setCurrentAddress(hashedIP);
        }

        Profile.Staff staff = profile.getStaffProfile();

        if (!profile.getCurrentAddress().equals(hashedIP)) {
            if (staff.isTwoFactor()) {
                if (!staff.isLocked()) {
                    staff.setLocked(true);
                }
            }

            profile.setCurrentAddress(hashedIP);
        }

        profile.save();

        Server server = Bukkit.getServer();

        if (server.hasWhitelist()) {
            if (server.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(event.getUniqueId()) || isElevated) {
                event.allow();
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Component.text("The server is currently whitelisted.")
                        .color(NamedTextColor.RED));
                return;
            }
        }

        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            if (profile.api().getAllPermissions().contains("flame.staff") || isElevated) {
                event.allow();
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, Component.text("The server is currently full.")
                        .color(NamedTextColor.RED));
                return;
            }
        }

        Profile.getProfiles().put(profile.getId(), profile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfile(player.getName(), player.getUniqueId());
        event.joinMessage(null);

        Util.setupBukkitPlayer(profile);

        boolean isElevated = Flame.getFlame().getElevatedStaff().contains(player.getUniqueId());

        if (isElevated) {
            player.setOp(true);
        }

        Tab.getTeamMap().values().forEach(scoreboardTeam -> {
            try {
                NMSUtil.setValue(scoreboardTeam, "i", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            NMSUtil.sendPacket(player, scoreboardTeam);
        });

        Tab.createTeam(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfiles().remove(player.getUniqueId());

        event.quitMessage(null);

        profile.setLastSeen(System.currentTimeMillis());

        profile.save();

        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(event.getPlayer());

        if (grantProcedure != null) {
            grantProcedure.cancel();
        }

        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(event.getPlayer());

        if (punishmentProcedure != null) {
            punishmentProcedure.cancel();
        }

        Profile.getProfiles().remove(profile.getId());

        Tab.deleteTeam(player);
    }

}
