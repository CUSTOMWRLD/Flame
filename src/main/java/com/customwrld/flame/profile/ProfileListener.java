package com.customwrld.flame.profile;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.Config;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import com.customwrld.flame.sync.AccountSync;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class ProfileListener implements Listener {

    private static Flame plugin = Flame.get();

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getPlayer(event.getUniqueId());

        if (player != null && player.isOnline()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.RED + "You tried to login too quickly after disconnecting.\nTry again in a few seconds.");
            plugin.getServer().getScheduler().runTask(plugin, () -> player.kickPlayer(CC.RED + "Duplicate login kick"));
            return;
        }

        Profile profile = null;

        try {
            profile = new Profile(event.getName(), event.getUniqueId());

            if (!profile.isLoaded()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(Config.FAILED_TO_LOAD_PROFILE.format());
                return;
            }

            if (profile.getActivePunishmentByType(PunishmentType.BLACKLIST) != null) {
                handlePunishment(event, profile.getActivePunishmentByType(PunishmentType.BLACKLIST));
                return;
            }

            if (profile.getActivePunishmentByType(PunishmentType.BAN) != null) {
                handlePunishment(event, profile.getActivePunishmentByType(PunishmentType.BAN));
                return;
            }

            profile.setName(event.getName());

            if (profile.getFirstSeen() == null) {
                profile.setFirstSeen(System.currentTimeMillis());
            }

            profile.setLastSeen(System.currentTimeMillis());

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(event.getAddress().getHostAddress().getBytes());
            BigInteger num = new BigInteger(1, hash);
            StringBuilder hashedIP = new StringBuilder(num.toString(16));
            while (hashedIP.length() < 32) {
                hashedIP.insert(0, "0");
            }

            if (profile.getCurrentAddress() == null) {
                profile.setCurrentAddress(hashedIP.toString());
            }

            if (!profile.getIpAddresses().contains(hashedIP.toString())) {
                profile.getIpAddresses().add(hashedIP.toString());
            }

            if (!profile.getCurrentAddress().equals(hashedIP.toString())) {
                if (profile.getStaffProfile().authEnabled()) {
                    if (!profile.getStaffProfile().locked()) {
                        profile.getStaffProfile().locked(true);
                    }
                }
                profile.setCurrentAddress(hashedIP.toString());
            }

            if (!profile.getCurrentAddress().equals(hashedIP.toString())) {
                List<Profile> alts = Profile.getByIpAddress(hashedIP.toString());

                for (Profile alt : alts) {
                    if (alt.getActivePunishmentByType(PunishmentType.BAN) != null) {
                        handlePunishment(event, alt.getActivePunishmentByType(PunishmentType.BAN));
                        return;
                    }
                }
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

        Profile.getProfiles().put(profile.getUuid(), profile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        profile.recalculatePlayer(player);

        event.setJoinMessage(null);

        if (profile.getStaffProfile().locked()) {
            Arrays.asList(CC.CHAT_BAR, CC.RED + "Please enter your 2FA Code!", CC.RED + "You will not be able to move or run commands until you verify yourself.", CC.CHAT_BAR).forEach(player::sendMessage);
        }

        AccountSync.resynchronize(profile);

//        NameMC.verify(profile, player);

        profile.setOnline(true);
        profile.setCurrentServer(CustomLib.getInstance().getServerName());
        profile.save();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfiles().remove(player.getUniqueId());

        profile.setLastSeen(System.currentTimeMillis());
        profile.setOnline(false);
        profile.setCurrentServer(null);

        if (profile.isLoaded()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(Flame.get());
        }

        AccountSync.resynchronize(profile);

        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(event.getPlayer());

        if (grantProcedure != null) {
            grantProcedure.cancel();
        }

        PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(event.getPlayer());

        if (punishmentProcedure != null) {
            punishmentProcedure.cancel();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfiles().remove(player.getUniqueId());

        event.setQuitMessage(null);

        profile.setLastSeen(System.currentTimeMillis());
        profile.setOnline(false);
        profile.setCurrentServer(null);

        if (profile.isLoaded()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(Flame.get());
        }

        AccountSync.resynchronize(profile);

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
        event.setKickMessage(String.join("\n", punishment.getKickMessage()));
    }

}
