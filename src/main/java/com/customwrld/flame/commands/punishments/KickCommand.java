package com.customwrld.flame.commands.punishments;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.listeners.ChatListener;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.Util;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

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
                    Player target = (Player) args[0];
                    String reason = (String) args[1];

                    if (sender instanceof Player executor) {
                        if (Util.isElevatedStaff(target.getUniqueId())) {
                            executor.sendMessage(Component.text("You cannot use that command in this context.")
                                    .color(MC.CC.RED.getTextColor()));
                            return;
                        }
                    }

                    CompletableFuture<Profile> future;
                    if(sender instanceof Player player) {
                        future = ProfileUtil.getAssociatedProfile(player);
                    } else {
                        future = CompletableFuture.completedFuture(null);
                    }

                    future.thenAccept(senderProfile -> {
                        ProfileUtil.getAssociatedProfile(target).thenAccept(targetProfile -> {
                            Component senderName = senderProfile != null ? ChatListener.hoverableFormat(senderProfile) : MC.Style.CONSOLE;

                            Component component = Component.text()
                                    .append(MC.Style.SEPARATOR)
                                    .append(MC.Chat.notificationMessage("Punishment", Component.text()
                                            .append(ChatListener.hoverableFormat(targetProfile))
                                            .append(Component.text(" has been kicked by ", MC.CC.GRAY.getTextColor())
                                                    .append(senderName))
                                            .build()))
                                    .append(MC.Style.SEPARATOR)
                                    .build();

                            Bukkit.getOnlinePlayers()
                                    .forEach(staff -> {
                                        if (target.hasPermission("flame.staff"))
                                            target.sendMessage(component);
                                    });
                        });
                    });

                    target.kick(MC.Style.fromLegacy(reason));
                });

    }

}
