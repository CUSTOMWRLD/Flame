package com.customwrld.flame.util;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisabledCommands implements Listener {

    private static final List<String> blockedCommands = new ArrayList<>();

    public static void Initialize() {
        Arrays.asList(
                "clear",
                "kill",
                "tps",
                "gamemode",
                "tell",
                "msg",
                "ban",
                "op",
                "deop",
                "ban-ip",
                "banlist",
                "defaultgamemode",
                "list",
                "me",
                "pardon",
                "pardon-ip",
                "say",
                "setidletimeout",
                "stop",
                "spreadplayers",
                "kick",
                "tag",
                "enchant").forEach(command -> CommandAPI.unregister(command, true));

        blockedCommands.addAll(Arrays.asList(
                "about",
                "ver",
                "pl",
                "plugins",
                "?",
                "restart",
                "help",
                "version",
                "icanhasbukkit",
                "execute",
                "tellraw"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void commandSend(PlayerCommandSendEvent event) {
        List<String> removeCommands = new ArrayList<>();
        event.getCommands().removeAll(blockedCommands);
        event.getCommands().forEach(command -> {
            if (command.contains(":")) {
                removeCommands.add(command);
            }
        });
        removeCommands.forEach(command -> event.getCommands().remove(command));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void commandProcess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split("\\s+");

        Player player = event.getPlayer();

        if (args[0].contains(":") || blockedCommands.contains(args[0].toLowerCase().substring(1))) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            event.setCancelled(true);
        }
    }

}
