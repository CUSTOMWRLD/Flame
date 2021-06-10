package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Util;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

public class KickCommand extends Command {

    public KickCommand() {
        this.command = "kick";
        this.permission = "flame.kick";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new PlayerArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    String reason = (String) args[1];

                    if (sender instanceof Player) {
                        Player executor = (Player) sender;

                        if (Util.isElevatedStaff(player, executor.getUniqueId())) {
                            return;
                        }
                    }

                    player.kickPlayer(CC.translate(reason));
                    sender.sendMessage(CC.GREEN + "You kicked " + player.getName() + " from the server.");
                });
    }

}
