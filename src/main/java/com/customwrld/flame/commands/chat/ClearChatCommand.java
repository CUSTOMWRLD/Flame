package com.customwrld.flame.commands.chat;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

                    Component playerName = MC.Style.CONSOLE;

                    if (sender instanceof Player) {
                        Player player = ((Player) sender);

                        Profile profile = Profile.getProfile(null, player.getUniqueId());

                        if (profile == null) {
                            return;
                        }

                        playerName = FlameAPI.getColoredName(profile);
                    }

                    Bukkit.broadcast(MC.Style.SEPARATOR);
                    Bukkit.broadcast(MC.Chat.notificationMessage("Chat",
                            Component.text("Chat has been cleared by ")
                                    .color(NamedTextColor.GRAY)
                                    .append(playerName)));
                    Bukkit.broadcast(MC.Style.SEPARATOR);
                });
    }

}
