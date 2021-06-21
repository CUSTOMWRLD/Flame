package com.customwrld.flame.commands.chat;

import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.flame.FlameAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

                    TextComponent playerName = Minecraft.Style.CONSOLE;

                    if (sender instanceof Player) {
                        Player player = ((Player) sender);

                        Profile profile = Profile.getProfile(null, player.getUniqueId());

                        if (profile == null) {
                            return;
                        }

                        playerName = FlameAPI.getColoredName(profile);
                    }

                    Bukkit.broadcast(Minecraft.Style.SEPARATOR);
                    Bukkit.broadcast(Minecraft.Style.getMessageComponent("Chat",
                            Component.text("Chat has been " + (FlameAPI.isChatMuted() ? "muted" : "unmuted") + " by ")
                                    .append(playerName)
                                    .color(NamedTextColor.GRAY)));
                    Bukkit.broadcast(Minecraft.Style.SEPARATOR);
                });
    }

}
