//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.FlameAPI;
//import dev.jorel.commandapi.CommandAPICommand;
//
//public class UnFreezeServerCommand extends Command {
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
//        return new CommandAPICommand("unfreezeserver")
//                .withPermission("flame.unfreezeserver")
//                .executes((sender, args) -> {
//                    if (FlameAPI.isServerFrozen()) { // TODO -> Add GUI Confirmation
//                        FlameAPI.setServerFrozen(false);
//                        sender.sendMessage(CC.GREEN + "You unfroze the server.");
//                    } else {
//                        sender.sendMessage(CC.RED + "The server is not frozen.");
//                    }
//                });
//    }
//
//}
