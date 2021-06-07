package com.customwrld.flame.commands.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        this.command = "clearchat";
        this.permission = "flame.clearchat";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.hasPermission("flame.staff")) {
                            for (int i = 0; i < 150; i++) {
                                player.sendMessage(" ");
                            }
                        }
                    }

                    String playerName = CC.CONSOLE;

                    if (sender instanceof Player) {
                        playerName = FlameAPI.getColoredName(Profile.getByUuid(((Player) sender).getUniqueId()));
                    }

                    Bukkit.broadcastMessage(Config.CLEAR_CHAT_BROADCAST.format(playerName));

                });
    }

}
