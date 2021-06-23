package com.customwrld.flame.commands.essentials;

import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                                    sender.sendMessage(Component.text("You must provide a player who's gamemode you want to set.")
                                            .color(NamedTextColor.RED));
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.CREATIVE);
                            player.sendMessage(Component.text("Your gamemode has been set to creative.")
                                    .color(NamedTextColor.GREEN));
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(Component.text("You have set " + player.getName() + "'s gamemode to creative.")
                                        .color(NamedTextColor.GREEN));
                            }
                        }))
                .withSubcommand(new CommandAPICommand("survival")
                        .withPermission(this.permission + ".survival")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(Component.text("You must provide a player who's gamemode you want to set.")
                                            .color(NamedTextColor.RED));
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(Component.text("Your gamemode has been set to survival.")
                                    .color(NamedTextColor.GREEN));
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(Component.text("You have set " + player.getName() + "'s gamemode to survival.")
                                        .color(NamedTextColor.GREEN));
                            }
                        }))
                .withSubcommand(new CommandAPICommand("adventure")
                        .withPermission(this.permission + ".adventure")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(Component.text("You must provide a player who's gamemode you want to set.")
                                            .color(NamedTextColor.RED));
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.ADVENTURE);
                            player.sendMessage(Component.text("Your gamemode has been set to adventure.")
                                    .color(NamedTextColor.GREEN));
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(Component.text("You have set " + player.getName() + "'s gamemode to adventure.")
                                        .color(NamedTextColor.GREEN));
                            }
                        }))
                .withSubcommand(new CommandAPICommand("spectator")
                        .withPermission(this.permission + ".spectator")
                        .withOptionalArgument(new PlayerArgument("player"), () -> null)
                        .executes((sender, args) -> {
                            Player player;

                            if (args.length == 0 || args[0] == null) {
                                if (sender instanceof ConsoleCommandSender) {
                                    sender.sendMessage(Component.text("You must provide a player who's gamemode you want to set.")
                                            .color(NamedTextColor.RED));
                                    return;
                                }
                                player = (Player) sender;
                            } else {
                                player = (Player) args[0];
                            }

                            player.setGameMode(GameMode.SPECTATOR);
                            player.sendMessage(Component.text("Your gamemode has been set to spectator.")
                                    .color(NamedTextColor.GREEN));
                            if (!(sender instanceof Player) && !(sender.getName().equals(player.getName()))) {
                                sender.sendMessage(Component.text("You have set " + player.getName() + "'s gamemode to spectator.")
                                        .color(NamedTextColor.GREEN));
                            }
                        }));
    }

}
