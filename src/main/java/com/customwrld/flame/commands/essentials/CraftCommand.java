package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.Config;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class CraftCommand extends Command {

    public CraftCommand() {
        this.command = "craft";
        this.permission = "flame.craft";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
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
