package com.customwrld.flame.commands.grant;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.grants.GrantsListMenu;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class GrantsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("grants")
                .withPermission("flame.grants")
                .withArguments(Arguments.profileArgument("player"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = (Profile) args[0];

                    new GrantsListMenu(player, profile);
                });
    }

}
