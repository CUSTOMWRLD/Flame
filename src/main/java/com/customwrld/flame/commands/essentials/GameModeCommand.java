package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("gamemode")
                .withPermission("flame.gamemode")
                .withSubcommand(new CommandAPICommand("creative")
                        .withPermission("flame.gamemode.creative")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(CC.RED + "You must provide a player who's gamemode you want to set.");
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.CREATIVE);
                            player.sendMessage(CC.GREEN + "Your gamemode has been set to creative.");
                            if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to creative.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("survival")
                        .withPermission("flame.gamemode.survival")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(CC.RED + "You must provide a player who's gamemode you want to set.");
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(CC.GREEN + "Your gamemode has been set to survival.");
                            if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to survival.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("adventure")
                        .withPermission("flame.gamemode.adventure")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(CC.RED + "You must provide a player who's gamemode you want to set.");
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.ADVENTURE);
                            player.sendMessage(CC.GREEN + "Your gamemode has been set to adventure.");
                            if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to adventure.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("spectator")
                        .withPermission("flame.gamemode.spectator")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(CC.RED + "You must provide a player who's gamemode you want to set.");
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.SPECTATOR);
                            player.sendMessage(CC.GREEN + "Your gamemode has been set to spectator.");
                            if(!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to spectator.");
                            }
                        }));
    }

}
