package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;

public class LockDownCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("lockdown")
                .withPermission("flame.lockdown")
                .executes((sender, args) -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!Flame.get().getElevatedStaff().contains(player.getUniqueId())) {
                            player.kickPlayer(CC.RED + "Network is currently in Lockdown.");
                        }
                    });
                });
    }
}
