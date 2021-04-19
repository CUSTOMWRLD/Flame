package com.customwrld.flame.commands.grant;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.grants.RankSelectionMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class GrantCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("grant")
                .withPermission("flame.grant")
                .withArguments(Arguments.profileArgument("player"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = (Profile) args[0];

                    new RankSelectionMenu(player, profile);
                });
    }

}
