//package com.customwrld.flame.commands.bungeecord;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.FlameAPI;
//import com.customwrld.flame.network.Server;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//
//import java.util.Arrays;
//
//public class FindCommand extends Command {
//
//    public FindCommand() {
//        this.command = "find";
//        this.permission = "flame.find";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withArguments(Arguments.proxyPlayerArgument("player"))
//                .executes((sender, args) -> {
//                    String player = (String) args[0];
//
//                    Profile profile = Profile.getByUsername(player);
//
//                    if (profile == null || !profile.isLoaded()) {
//                        sender.sendMessage(CC.RED + Config.COULD_NOT_RESOLVE_PLAYER.format());
//                        return;
//                    }
//
//                    Server server = Server.getServerByPlayer(player);
//
//                    if (server == null) {
//                        sender.sendMessage(CC.RED + "That player is not online!");
//                        return;
//                    }
//
//                    Arrays.asList(
//                            CC.CHAT_BAR,
//                            FlameAPI.getColoredName(profile) + CC.GRAY + " is online in " + server.getServerName() + ".",
//                            CC.CHAT_BAR
//                    ).forEach(message -> sender.sendMessage(CC.translate(message)));
//                });
//    }
//
//}
