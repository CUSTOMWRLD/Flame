package com.customwrld.flame.commands.options;

import com.customwrld.flame.Config;
import com.customwrld.flame.menus.general.ProfileOptionsMenu;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

public class OptionsCommand extends Command {

    public OptionsCommand() {
        this.command = "options";
        this.permission = "flame.options";
        this.aliases = new String[]{"settings"};
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withAliases(this.aliases)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    new ProfileOptionsMenu((Player) sender);
                });
    }

}
