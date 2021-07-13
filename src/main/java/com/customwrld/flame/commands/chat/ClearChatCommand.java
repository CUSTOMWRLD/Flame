package com.customwrld.flame.commands.chat;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.listeners.ChatListener;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        this.command = "clearchat";
        this.permission = "flame.clearchat";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.hasPermission("flame.staff")) {
                            for (int i = 0; i < 150; i++) {
                                player.sendMessage(" ");
                            }
                        }
                    }

                    CompletableFuture<Profile> future;
                    if(sender instanceof Player player) {
                        future = ProfileUtil.getAssociatedProfile(player);
                    } else {
                        future = CompletableFuture.completedFuture(null);
                    }

                    future.thenAccept(profile -> {
                        Component playerName = profile != null ? ChatListener.hoverableFormat(profile) : MC.Style.CONSOLE;

                        var component = Component.text()
                                .append(MC.Style.SEPARATOR)
                                .append(MC.Chat.notificationMessage("Chat",
                                        Component.text("Chat has been cleared by ", MC.CC.GRAY.getTextColor())
                                                .append(playerName)))
                                .append(MC.Style.SEPARATOR)
                                .build();

                        Bukkit.broadcast(component);
                    });

                });
    }

}
