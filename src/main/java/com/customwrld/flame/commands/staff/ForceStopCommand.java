//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import dev.jorel.commandapi.CommandAPICommand;
//import org.bukkit.Bukkit;
//
//public class ForceStopCommand extends Command {
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
//        return new CommandAPICommand("forcestop")
//                .withPermission("flame.forcestop")
//                .executes((sender, args) -> {
//                    Bukkit.broadcastMessage(CC.RED + "Server is currently being rebooted!");
//                    Bukkit.shutdown();
//                });
//    }
//
//}
