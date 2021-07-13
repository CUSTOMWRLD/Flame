package com.customwrld.flame.commands.grant;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.grants.GrantsListMenu;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class GrantsCommand extends Command {

    public GrantsCommand() {
        this.command = "grants";
        this.permission = "flame.grants";
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

                    new GrantsListMenu(player, profile);
                });
    }

}
