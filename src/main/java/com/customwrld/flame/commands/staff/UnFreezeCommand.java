//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.FlameAPI;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.PlayerArgument;
//import org.bukkit.entity.Player;
//
//public class UnFreezeCommand extends Command {
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
//        return new CommandAPICommand("unfreeze")
//                .withPermission("flame.unfreeze")
//                .withArguments(new PlayerArgument("player"))
//                .executes((sender, args) -> {
//                    Player player = (Player) args[0];
//
//                    if (FlameAPI.getFrozenPlayers().contains(player.getUniqueId())) {
//                        FlameAPI.getFrozenPlayers().remove(player.getUniqueId());
//                        sender.sendMessage(CC.GREEN + player.getName() + " has been unfrozen.");
//                    } else {
//                        sender.sendMessage(CC.RED + "That player is already unfrozen.");
//                    }
//                });
//    }
//
//}
