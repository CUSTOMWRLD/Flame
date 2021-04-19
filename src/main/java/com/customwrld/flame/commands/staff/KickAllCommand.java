package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.util.BungeeUtil;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;

public class KickAllCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("kickall")
                .withPermission("flame.kickall")
                .executes((sender, args) -> {
                    List<String> hubs = Flame.get().getMainConfig().getStringList("HUBS");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!player.hasPermission("flame.staff")) {
                            player.sendMessage(CC.GREEN + "You are currently being connected to a fallback server.");
                            BungeeUtil.sendPlayer(player.getName(), hubs.get(new Random().nextInt(hubs.size())));
                        }
                    });

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!player.hasPermission("flame.staff")) {
                            player.kickPlayer(CC.RED + "Could not send you to a fallback server.");
                        }
                    });
                });
    }

}
