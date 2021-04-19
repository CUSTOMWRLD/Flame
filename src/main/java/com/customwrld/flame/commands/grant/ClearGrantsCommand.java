package com.customwrld.flame.commands.grant;

import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.RecalculatePlayerPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.UUID;

public class ClearGrantsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("cleargrants")
                .withPermission(CommandPermission.OP)
                .withArguments(Arguments.profileArgument("player"))
                .executes((sender, args) -> {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Config.CONSOLE_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    profile.getGrants().clear();
                    profile.getGrants().add(new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null, System.currentTimeMillis(), "Default", Integer.MAX_VALUE));
                    profile.checkGrants();
                    profile.save();

                    new RecalculatePlayerPayload(profile.getUuid()).send();

                    sender.sendMessage(ChatColor.GREEN + "Cleared grants of " + profile.getName() + "!");
                });
    }

}
