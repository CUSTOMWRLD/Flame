package com.customwrld.flame.commands.punishments;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.punishments.PunishmentSelectMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class HistoryCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("history")
                .withPermission("flame.history")
                .withArguments(Arguments.profileArgument("player"))
                .withAliases("his", "punishments")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = (Profile) args[0];

                    if (profile == null || !profile.isLoaded()) {
                        player.sendMessage(Config.COULD_NOT_RESOLVE_PLAYER.format());
                        return;
                    }

                    new PunishmentSelectMenu(player, profile);
                });
    }

}
