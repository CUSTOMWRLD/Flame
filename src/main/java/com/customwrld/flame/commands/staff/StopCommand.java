package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.util.BungeeUtil;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Initialize;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class StopCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("stop")
                .withPermission("flame.stop")
                .executes((sender, args) -> {
                    Flame.get().setShuttingDown(true);
                    Bukkit.broadcastMessage(CC.RED + "Server is currently being rebooted!");

                    Profile.getProfiles().values().forEach(profile -> {
                        if (profile != null && profile.isLoaded()) {
                            profile.setLastSeen(System.currentTimeMillis());
                            profile.setCurrentServer(null);
                            profile.setOnline(false);
                            profile.save();
                        }
                    });

                    List<String> hubs = Flame.get().getMainConfig().getStringList("HUBS");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendMessage(CC.GREEN + "You are currently being connected to a fallback server.");
                        BungeeUtil.sendPlayer(player.getName(), hubs.get(new Random().nextInt(hubs.size())));
                    });

                    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(CC.RED + "Could not send you to a fallback server."));

                    Initialize.taskList.forEach(BukkitTask::cancel);
                    Bukkit.getServer().getScheduler().cancelTasks(Flame.get());

                    Bukkit.shutdown();
                });
    }

}