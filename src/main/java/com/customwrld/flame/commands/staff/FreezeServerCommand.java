//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.FlameAPI;
//import dev.jorel.commandapi.CommandAPICommand;
//
//public class FreezeServerCommand extends Command {
//
//    public replace() {
//        this.command = "replace";
//        this.permission = "flame.replace";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return null;
//    }
//
//    public static CommandAPICommand command() {
//        return new CommandAPICommand("freezeserver")
//                .withPermission("flame.freezeserver")
//                .executes((sender, args) -> {
//                    if (FlameAPI.isServerFrozen()) {
//                        sender.sendMessage(CC.RED + "The server is already frozen.");
//                    } else { // TODO -> Add GUI Confirmation
//                        FlameAPI.setServerFrozen(true);
//                        sender.sendMessage(CC.GREEN + "You have frozen the server.");
//                    }
//                });
//    }
//
//}
