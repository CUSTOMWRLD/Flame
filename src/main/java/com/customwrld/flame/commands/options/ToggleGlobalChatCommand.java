//package com.customwrld.flame.commands.options;
//
//import com.customwrld.flame.Config;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.CommandPermission;
//import org.bukkit.entity.Player;
//
//public class ToggleGlobalChatCommand extends Command {
//
//    public replace() {
//        this.command = "replace";
//        this.permission = "flame.replace";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return null;
//    }
//
//    public static CommandAPICommand command() {
//        return new CommandAPICommand("toggleglobalchat")
//                .withPermission(CommandPermission.NONE)
//                .withAliases("togglepublicchat")
//                .executes((sender, args) -> {
//                    if (!(sender instanceof Player)) {
//                        sender.sendMessage(Config.PLAYER_ONLY.format());
//                        return;
//                    }
//
//                    Player player = (Player) sender;
//
//                    Profile profile = Profile.getByUuid(player.getUniqueId());
//                    profile.getOptions().publicChatEnabled(!profile.getOptions().publicChatEnabled());
//
//                    if (profile.getOptions().publicChatEnabled()) {
//                        player.sendMessage(Config.OPTIONS_GLOBAL_CHAT_ENABLED.format());
//                    } else {
//                        player.sendMessage(Config.OPTIONS_GLOBAL_CHAT_DISABLED.format());
//                    }
//                });
//    }
//
//}
