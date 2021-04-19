package com.customwrld.flame.commands.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MuteChatCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("mutechat")
                .withPermission("flame.mutechat")
                .executes((sender, args) -> {
                    Flame flame = Flame.get();
                    flame.setChatMuted(!flame.isChatMuted());

                    String playerName = CC.CONSOLE;

                    if (sender instanceof Player) {
                        playerName = FlameAPI.getColoredName(Profile.getByUuid(((Player) sender).getUniqueId()));
                    }

                    if (flame.isChatMuted()) {
                        Bukkit.broadcastMessage(Config.CHAT_MUTED.format(playerName));
                    } else {
                        Bukkit.broadcastMessage(Config.CHAT_UNMUTED.format(playerName));
                    }
                });
    }

}
