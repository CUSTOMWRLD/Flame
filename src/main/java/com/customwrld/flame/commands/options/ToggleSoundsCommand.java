package com.customwrld.flame.commands.options;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("togglesounds")
                .withPermission(CommandPermission.NONE)
                .withAliases("sounds")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = Profile.getByUuid(player.getUniqueId());

                    profile.getOptions().soundsEnabled(!profile.getOptions().soundsEnabled());

                    if (profile.getOptions().soundsEnabled()) {
                        player.sendMessage(Config.OPTIONS_PRIVATE_MESSAGE_SOUND_ENABLED.format());
                    } else {
                        player.sendMessage(Config.OPTIONS_PRIVATE_MESSAGE_SOUND_DISABLED.format());
                    }
                });
    }

}
