package com.customwrld.flame.commands.chat;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.listeners.ChatListener;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class MuteChatCommand extends Command {

    public MuteChatCommand() {
        this.command = "mutechat";
        this.permission = "flame.mutechat";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    FlameAPI.setChatMuted(!FlameAPI.isChatMuted());

                    CompletableFuture<Profile> future;
                    if (sender instanceof Player player) {
                        future = ProfileUtil.getAssociatedProfile(player);
                    } else {
                        future = CompletableFuture.completedFuture(null);
                    }

                    future.thenAccept(profile -> {
                        var playerName = profile != null ? ChatListener.hoverableFormat(profile) : MC.Style.CONSOLE;

                        var component = Component.text()
                                .append(MC.Style.SEPARATOR)
                                .append(MC.Chat.notificationMessage("Chat",
                                        Component.text("Chat has been " + (FlameAPI.isChatMuted() ? "muted" : "unmuted") + " by ")
                                                .append(playerName)
                                                .color(NamedTextColor.GRAY)))
                                .append(MC.Style.SEPARATOR)
                                .build();

                        Bukkit.broadcast(component);
                    });
                });
    }

}
