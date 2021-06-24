package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Util;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickCommand extends Command {

    public KickCommand() {
        this.command = "kick";
        this.permission = "flame.kick";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(new PlayerArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    String reason = (String) args[1];

                    if (sender instanceof Player) {
                        Player executor = (Player) sender;

                        if (Util.isElevatedStaff(executor, player.getUniqueId())) {
                            return;
                        }
                    }

                    Component playerName = MC.Style.CONSOLE;

                    if (sender instanceof Player) {
                        playerName = FlameAPI.getColoredName(Profile.getProfile(null, ((Player) sender).getUniqueId()));
                    }

                    Component punishmentMessage = MC.Chat.notificationMessage("Punishment", Component.text()
                            .append(FlameAPI.getColoredName(Profile.getProfile(null, player.getUniqueId())))
                            .append(Component.text(" has been kicked by ")
                                    .color(NamedTextColor.GRAY))
                            .append(playerName)
                            .build());

                    Bukkit.getOnlinePlayers()
                            .forEach(staff -> {
                                if (player.hasPermission("flame.staff")) {
                                    player.sendMessage(MC.Style.SEPARATOR);
                                    player.sendMessage(punishmentMessage);
                                    player.sendMessage(MC.Style.SEPARATOR);
                                }
                            });

                    player.kick(MC.Style.fromLegacy(reason));
                });

    }

}
