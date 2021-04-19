package com.customwrld.flame.commands.player;

import com.customwrld.flame.Config;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.sync.AccountSync;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.entity.Player;

public class DesyncCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("desync")
                .withPermission(CommandPermission.NONE)
                .withSubcommand(new CommandAPICommand("discord")
                        .executes((sender, args) -> {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Config.PLAYER_ONLY.format());
                                return;
                            }

                            Player player = (Player) args[0];
                            Profile profile = Profile.getByUuid(player.getUniqueId());

                            AccountSync.getDiscordSync().desync(profile);
                        }));
    }

}
