package com.customwrld.flame.commands.options;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.general.ProfileOptionsMenu;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

public class OptionsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("options")
                .withAliases("settings")
                .withPermission(CommandPermission.NONE)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    new ProfileOptionsMenu((Player) sender);
                });
    }

}
