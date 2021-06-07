//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.FlameAPI;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.PlayerArgument;
//import org.bukkit.entity.Player;
//
//public class FreezeCommand extends Command {
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
//        return new CommandAPICommand("freeze")
//                .withPermission("flame.freeze")
//                .withArguments(new PlayerArgument("player"))
//                .executes((sender, args) -> {
//                    Player player = (Player) args[0];
//
//                    if (FlameAPI.getFrozenPlayers().contains(player.getUniqueId())) {
//                        sender.sendMessage(CC.RED + player.getName() + " is already frozen.");
//                    } else {
//                        FlameAPI.getFrozenPlayers().add(player.getUniqueId());
//                        sender.sendMessage(CC.GREEN + "You have frozen " + player.getName() + ".");
//                    }
//                });
//    }
//
//}
