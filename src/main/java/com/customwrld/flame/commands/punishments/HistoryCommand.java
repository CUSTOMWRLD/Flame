package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.punishments.PunishmentSelectMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        this.command = "history";
        this.permission = "flame.history";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(Arguments.profileArgument("player"))
                .withAliases("his", "punishments")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = (Profile) args[0];

                    new PunishmentSelectMenu(player, profile);
                });
    }

}
