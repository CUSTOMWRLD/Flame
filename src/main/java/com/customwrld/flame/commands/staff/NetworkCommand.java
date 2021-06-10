//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.flame.menus.general.NetworkInfoMenu;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//
//public class NetworkCommand extends Command {
//
//    public NetworkCommand() {
//        this.command = "network";
//        this.permission = "flame.network";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .executesPlayer((player, args) -> {
//                    new NetworkInfoMenu(player);
//                });
//    }
//
//}
