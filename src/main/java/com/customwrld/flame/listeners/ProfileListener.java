package com.customwrld.flame.listeners;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.NMSUtil;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProfileListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            Flame flame = Flame.getFlame();

            if(FlameAPI.isStartingUp()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text("Server is currently starting up. Please join back soon.")
                                .color(NamedTextColor.RED));
                return;
            }

            var uuid = event.getUniqueId();

            Player player = Bukkit.getPlayer(uuid);

            if(player != null && player.isOnline()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text("You tried to login too quickly after disconnecting.", MC.CC.RED.getTextColor())
                                .append(Component.newline())
                                .append(Component.text("Please try again in a few seconds.", MC.CC.RED.getTextColor())));

                flame.getServer().getScheduler().runTask(flame, () ->
                        player.kick(Component.text("You logged in from another location!", MC.CC.RED.getTextColor())));
                return;
            }

            boolean isElevated = Util.isElevatedStaff(uuid);

            var name = event.getName();

            Profile profile = ProfileUtil.getProfile(name, uuid).get(10, TimeUnit.SECONDS);

            Punishment punishment = profile.api().getActiveBan();

            if(punishment != null && !isElevated) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                        Component.join(Component.newline(), Util.getPunishmentMessage(punishment)));
                return;
            }

            profile.setName(name);
            profile.setSearchableName(name);

            if(profile.getFirstSeen() == null)
                profile.setFirstSeen(System.currentTimeMillis());

            profile.setLastSeen(System.currentTimeMillis());

            String hashedIP = Hashing.sha1().hashString(event.getAddress().getHostAddress(), StandardCharsets.UTF_8).toString();

            if(profile.getCurrentAddress() == null)
                profile.setCurrentAddress(hashedIP);

            Profile.Staff staff = profile.getStaffProfile();

            if(!profile.getCurrentAddress().equals(hashedIP)) {
                if(staff.isTwoFactor() && !staff.isLocked())
                    staff.setLocked(true);

                profile.setCurrentAddress(hashedIP);
            }

            profile.update();

            Server server = Bukkit.getServer();

            if(server.hasWhitelist()) {
                if(server.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(uuid) || isElevated) {
                    event.allow();
                } else {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                            Component.text("The server is currently whitelisted.", MC.CC.RED.getTextColor()));
                    return;
                }
            }

            if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
                if(profile.api().getAllPermissions().contains("flame.staff") || isElevated) {
                    event.allow();
                } else {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,
                            Component.text("The server is currently full.", MC.CC.RED.getTextColor()));
                }
            }
        } catch(ExecutionException | InterruptedException | TimeoutException e) {
            Thread.currentThread().interrupt();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("Failed to load your profile. Try again later.", MC.CC.RED.getTextColor()));
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            Util.setupBukkitPlayer(profile);

            if (Util.isElevatedStaff(player.getUniqueId()))
                player.setOp(true);

            Tab.getTeamMap().values().forEach(scoreboardTeam -> {
                try {
                    NMSUtil.setValue(scoreboardTeam, "i", 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                NMSUtil.sendPacket(player, scoreboardTeam);
            });

            Tab.createTeam(player);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(null);

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            profile.setLastSeen(System.currentTimeMillis());

            profile.update();

            GrantProcedure grantProcedure = GrantProcedure.getByPlayer(event.getPlayer());

            if (grantProcedure != null)
                grantProcedure.cancel();

            PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(event.getPlayer());

            if (punishmentProcedure != null)
                punishmentProcedure.cancel();

            Tab.deleteTeam(player);
        });
    }

}
