package com.customwrld.flame.commands.bungeecord;

import com.customwrld.customlib.util.BungeeUtil;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.ServerMessagePlayerPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.network.Server;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.Arrays;

public class SendCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("send")
                .withPermission("flame.send")
                .withArguments(Arguments.proxyPlayerArgument("player"))
                .withArguments(Arguments.serverArgument("server"))
                .executes((sender, args) -> {
                    Profile profile = Profile.getByUsername((String) args[0]);
                    Server server = (Server) args[1];

                    if (profile == null || !profile.isLoaded()) {
                        sender.sendMessage(CC.RED + Config.COULD_NOT_RESOLVE_PLAYER.format());
                        return;
                    }

                    new ServerMessagePlayerPayload(profile.getName(), "&6You are being sent to " + server.getServerName() + "...").send();

                    Arrays.asList(
                            CC.CHAT_BAR,
                            FlameAPI.getColoredName(profile) + CC.GRAY + " has been sent to " + server.getServerName() + ".",
                            CC.CHAT_BAR).forEach(message -> sender.sendMessage(CC.translate(message)));

                    BungeeUtil.sendPlayer(profile.getName(), server.getServerName());
                });
    }

}
