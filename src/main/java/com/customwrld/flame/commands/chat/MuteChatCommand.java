package com.customwrld.flame.commands.chat;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MuteChatCommand extends Command {

    public MuteChatCommand() {
        this.command = "mutechat";
        this.permission = "flame.mutechat";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    Flame flame = Flame.getFlame();
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
