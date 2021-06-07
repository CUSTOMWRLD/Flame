package com.customwrld.flame.commands.staff.listener;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.FlameAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (FlameAPI.isServerFrozen() || FlameAPI.getFrozenPlayers().contains(event.getPlayer().getUniqueId()) && (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()))
            event.getPlayer().teleport(event.getFrom());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (FlameAPI.isServerFrozen() || FlameAPI.getFrozenPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can not place blocks while you are frozen.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (FlameAPI.isServerFrozen() || FlameAPI.getFrozenPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can not break blocks while you are frozen.");
        }
    }

    public Player getDamager(Entity entity) {
        if (entity instanceof Player)
            return (Player) entity;
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() != null && projectile.getShooter() instanceof Player)
                return (Player) projectile.getShooter();
        }
        return null;
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (FlameAPI.getFrozenPlayers().contains(player.getUniqueId()))
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.hasPermission("flame.staff")) {
                    players.sendMessage(CC.CHAT_BAR);
                    players.sendMessage(CC.RED + player.getName() + " has logged out while frozen!");
                    players.sendMessage(CC.CHAT_BAR);
                }
            }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damager = getDamager(event.getDamager());
        Player damaged = getDamager(event.getEntity());
        if (damager != null && damaged != null && damaged != damager) {
            if (FlameAPI.isServerFrozen() || FlameAPI.getFrozenPlayers().contains(damager.getUniqueId())) {
                damager.sendMessage(CC.RED + "You can not attack players while frozen.");
                event.setCancelled(true);
            }
            if (FlameAPI.isServerFrozen() || FlameAPI.getFrozenPlayers().contains(damaged.getUniqueId())) {
                damager.sendMessage(CC.RED + damaged.getName() + " is currently frozen and cannot be damaged.");
                event.setCancelled(true);
            }
        }
    }
}
