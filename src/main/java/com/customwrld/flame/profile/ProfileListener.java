package com.customwrld.flame.profile;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.google.common.hash.Hashing;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.stream.Collectors;

public class ProfileListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Flame flame = Flame.getFlame();
        Player player = Bukkit.getPlayer(event.getUniqueId());

        if (FlameAPI.isStartingUp()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "Server is currently starting up. Please join back soon.");
            return;
        }

        if (player != null && player.isOnline()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.RED + "You tried to login too quickly after disconnecting.\nTry again in a few seconds.");
            flame.getServer().getScheduler().runTask(flame, () -> player.kickPlayer(CC.RED + "You logged in from another location!"));
            return;
        }

        boolean isElevated = flame.getElevatedStaff().contains(event.getUniqueId());

        Profile profile = null;

        try {
            profile = new Profile(event.getName(), event.getUniqueId());

            if (!profile.isLoaded()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(Config.FAILED_TO_LOAD_PROFILE.format());
                return;
            }

            if (profile.getActivePunishmentByType(Punishment.Type.BLACKLIST) != null && !isElevated) {
                handlePunishment(event, profile.getActivePunishmentByType(Punishment.Type.BLACKLIST));
                return;
            }

            if (profile.getActivePunishmentByType(Punishment.Type.BAN) != null && !isElevated) {
                handlePunishment(event, profile.getActivePunishmentByType(Punishment.Type.BAN));
                return;
            }

            profile.setName(event.getName());

            if (profile.getFirstSeen() == null) {
                profile.setFirstSeen(System.currentTimeMillis());
            }

            profile.setLastSeen(System.currentTimeMillis());

            String hashedIP = Hashing.sha256().hashString(event.getAddress().getHostAddress(), StandardCharsets.UTF_8).toString();

            if (profile.getCurrentAddress() == null) {
                profile.setCurrentAddress(hashedIP);
            }

            if (!profile.getCurrentAddress().equals(hashedIP)) {
                if (profile.getStaffProfile().isTwoFactor()) {
                    if (!profile.getStaffProfile().isLocked()) {
                        profile.getStaffProfile().setLocked(true);
                    }
                }

                profile.setCurrentAddress(hashedIP);
            }

            profile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile == null || !profile.isLoaded()) {
            event.setKickMessage(Config.FAILED_TO_LOAD_PROFILE.format());
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        Server server = Bukkit.getServer();

        if (server.hasWhitelist()) {
            if (server.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(event.getUniqueId()) || isElevated) {
                event.allow();
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, CC.RED + "The server is currently whitelisted!");
            }
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            if (profile.getAllPermissions().contains("flame.staff") || isElevated) {
                event.allow();
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, CC.RED + "The server is currently full! Please try again later.");
            }
            return;
        }

        Profile.getProfiles().put(profile.getUuid(), profile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);

        boolean isElevated = Flame.getFlame().getElevatedStaff().contains(player.getUniqueId());

        if (isElevated) {
            player.setOp(true);
        }

//        AccountSync.resynchronize(profile);

//        NameMC.verify(profile, player);

//        profile.saveAsync();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfiles().remove(player.getUniqueId());

        event.setQuitMessage(null);

        profile.setLastSeen(System.currentTimeMillis());

        profile.save();

//        AccountSync.resynchronize(profile);

        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(event.getPlayer());

        if (grantProcedure != null) {
            grantProcedure.cancel();
        }

        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(event.getPlayer());

        if (punishmentProcedure != null) {
            punishmentProcedure.cancel();
        }
    }

    private void handlePunishment(AsyncPlayerPreLoginEvent event, Punishment punishment) {
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(String.join("\n", punishment.getPunishmentMessage()));
    }

}
