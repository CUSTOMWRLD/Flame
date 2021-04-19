package com.customwrld.flame.commands.player;

import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.customlib.util.BungeeUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class HubCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("hub")
                .withPermission(CommandPermission.NONE)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;
                    ConfigurationFile config = Flame.get().getMainConfig();
                    List<String> hubs = config.getStringList("HUBS");

                    String server = hubs.get(new Random().nextInt(hubs.size()));

                    BungeeUtil.sendPlayer(player, server);
                });
    }

}
