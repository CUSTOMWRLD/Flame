package com.customwrld.flame.commands.staff;

import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.List;

public class StopCommand extends Command {

    public StopCommand() {
        this.command = "stop";
        this.permission = "flame.stop";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand("stop")
                .withPermission("flame.stop")
                .executes((sender, args) -> {
                    FlameAPI.setShuttingDown(true);
                    Bukkit.broadcast(Component.text("Server is currently being rebooted!")
                            .color(MC.CC.RED.getTextColor()));


                    List<String> hubs = Flame.getFlame().getMainConfig().getStringList("HUBS");

                    Bukkit.getOnlinePlayers().forEach(player -> {

                        var profile = ProfileUtil.getAssociatedProfile(player, true, false).getNow(null);
                        if(profile != null)
                            profile.update();

                        player.sendMessage(Component.text("You are currently being connected to a fallback server.")
                                .color(MC.CC.GREEN.getTextColor()));
                    });

                    Bukkit.shutdown();
                });
    }

}