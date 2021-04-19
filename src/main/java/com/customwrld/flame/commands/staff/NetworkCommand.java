package com.customwrld.flame.commands.staff;

import com.customwrld.flame.menus.general.NetworkInfoMenu;
import dev.jorel.commandapi.CommandAPICommand;

public class NetworkCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("network")
                .withPermission("flame.network")
                .executesPlayer((player, args) -> {
                    new NetworkInfoMenu(player);
                });
    }

}
