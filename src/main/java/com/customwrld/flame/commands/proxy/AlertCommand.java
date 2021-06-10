//package com.customwrld.flame.commands.proxy;
//
//import com.customwrld.flame.pigeon.payloads.BroadcastMessagePayload;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.GreedyStringArgument;
//
//public class AlertCommand extends Command {
//
//    public AlertCommand() {
//        this.command = "alert";
//        this.permission = "flame.alert";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withArguments(new GreedyStringArgument("message"))
//                .executes((sender, args) -> {
//                    String message = (String) args[0];
//
//                    new BroadcastMessagePayload(message).send();
//                });
//    }
//
//}
