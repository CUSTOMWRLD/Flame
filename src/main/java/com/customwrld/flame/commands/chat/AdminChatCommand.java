package com.customwrld.flame.commands.chat;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.chat.StaffChatEnum;
import com.customwrld.flame.pigeon.payloads.StaffChatPayload;
import com.customwrld.flame.profile.Profile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class AdminChatCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("adminchat")
                .withPermission("flame.adminchat")
                .withArguments(new GreedyStringArgument("message"))
                .withAliases("ac")
                .executes((sender, args) -> {
                    String playerName = CC.CONSOLE;

                    if (sender instanceof Player) {
                        Profile profile = Profile.getByUuid(((Player) sender).getUniqueId());

                        if (!profile.getStaffProfile().staffMessagesEnabled()) {
                            sender.sendMessage(CC.RED + "You currently have staff messages toggled.");
                            return;
                        }

                        playerName = FlameAPI.getColoredName(profile);
                    }

                    new StaffChatPayload(StaffChatEnum.ADMIN_CHAT, CustomLib.getInstance().getServerName(), playerName, (String) args[0]).send();
                });
    }

}
