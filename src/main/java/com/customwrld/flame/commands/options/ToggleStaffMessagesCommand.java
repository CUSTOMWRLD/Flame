package com.customwrld.flame.commands.options;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.StaffProfile;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class ToggleStaffMessagesCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("togglestaffmessages")
                .withPermission("flame.togglestaffmessages")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    StaffProfile profile = Profile.getByUuid(player.getUniqueId()).getStaffProfile();

                    profile.staffMessagesEnabled(!profile.staffMessagesEnabled());

                    if (profile.staffMessagesEnabled()) {
                        player.sendMessage(Config.OPTIONS_STAFF_CHAT_MESSAGES_ENABLED.format());
                    } else {
                        player.sendMessage(Config.OPTIONS_STAFF_CHAT_MESSAGES__DISABLED.format());
                    }
                });
    }

}
