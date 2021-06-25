package com.customwrld.flame.listeners;

import org.bukkit.event.Listener;

public class AuthListener implements Listener {

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void PlayerMoveEvent(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        Profile profile = Profile.getByUuid(player.getUniqueId());
//
//        if (profile.getStaffProfile().isLocked()) {
//            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
//                event.getPlayer().teleport(event.getFrom());
//                player.sendActionBar(CC.RED + "Please enter your Two Factor Authentication Code!");
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void PlayerInteractEvent(PlayerInteractEvent event) {
//        Player player = event.getPlayer();
//        Profile profile = Profile.getByUuid(player.getUniqueId());
//
//        if (profile.getStaffProfile().isLocked()) {
//            event.setCancelled(true);
//            player.sendActionBar(CC.RED + "Please enter your Two Factor Authentication Code!");
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void EntityDamageEvent(EntityDamageByEntityEvent event) {
//        if (event.getDamager() instanceof Player) {
//            Player player = (Player) event.getDamager();
//            Profile profile = Profile.getByUuid(player.getUniqueId());
//
//            if (profile.getStaffProfile().isLocked()) {
//                event.setCancelled(true);
//                player.sendActionBar(CC.RED + "Please enter your Two Factor Authentication Code!");
//            }
//        }
//
//        if (event.getEntity() instanceof Player) {
//            Player player = (Player) event.getEntity();
//            Profile profile = Profile.getByUuid(player.getUniqueId());
//
//            if (profile.getStaffProfile().isLocked()) {
//                event.setCancelled(true);
//                player.sendActionBar(CC.RED + "Please enter your Two Factor Authentication Code!");
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
//        Player player = event.getPlayer();
//        Profile profile = Profile.getByUuid(player.getUniqueId());
//
//        if (profile.getStaffProfile().isLocked()) {
//            event.setCancelled(true);
//            player.sendActionBar(CC.RED + "Please enter your Two Factor Authentication Code!");
//        }
//    }
}