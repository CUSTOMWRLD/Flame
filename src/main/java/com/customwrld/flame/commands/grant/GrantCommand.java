package com.customwrld.flame.commands.grant;

import com.customwrld.flame.Config;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.menus.grants.RankSelectionMenu;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class GrantCommand extends Command {

    public GrantCommand() {
        this.command = "grant";
        this.permission = "flame.grant";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(Arguments.profileArgument("player"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    new RankSelectionMenu(player, profile);
                });
    }

}
