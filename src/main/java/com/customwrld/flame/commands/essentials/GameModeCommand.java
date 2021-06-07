package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand extends Command {

    public GameModeCommand() {
        this.command = "gamemode";
        this.permission = "flame.gamemode";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withSubcommand(new CommandAPICommand("creative")
                        .withPermission(this.permission + ".creative")
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
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to creative.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("survival")
                        .withPermission(this.permission + ".survival")
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
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to survival.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("adventure")
                        .withPermission(this.permission + ".adventure")
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
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to adventure.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("spectator")
                        .withPermission(this.permission + ".spectator")
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
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(CC.GREEN + "You have set " + player.getName() + "'s gamemode to spectator.");
                            }
                        }));
    }

}
