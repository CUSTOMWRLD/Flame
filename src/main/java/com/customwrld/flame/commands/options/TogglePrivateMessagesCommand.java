package com.customwrld.flame.commands.options;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

public class TogglePrivateMessagesCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("togglepm")
                .withPermission(CommandPermission.NONE)
                .withAliases("togglepms", "tpm", "tpms")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    Profile profile = Profile.getByUuid(player.getUniqueId());

                    profile.getOptions().conversationsEnabled(!profile.getOptions().conversationsEnabled());
                    profile.getConversations().expireAllConversations();

                    if (profile.getOptions().conversationsEnabled()) {
                        player.sendMessage(Config.OPTIONS_PRIVATE_MESSAGES_ENABLED.format());
                    } else {
                        player.sendMessage(Config.OPTIONS_PRIVATE_MESSAGES_DISABLED.format());
                    }
                });
    }

}
