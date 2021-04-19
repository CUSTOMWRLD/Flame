package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class CraftCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("craft")
                .withPermission("flame.craft")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    player.openWorkbench(null, true);
                });
    }

}
