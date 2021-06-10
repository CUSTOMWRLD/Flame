//package com.customwrld.flame.commands.proxy;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.FlameAPI;
//import com.customwrld.flame.network.Server;
//import com.customwrld.flame.pigeon.payloads.ProxyCommandPayload;
//import com.customwrld.flame.pigeon.payloads.ServerMessagePlayerPayload;
//import com.customwrld.flame.profile.Profile;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//
//public class SendCommand extends Command {
//
//    public SendCommand() {
//        this.command = "send";
//        this.permission = "flame.send";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withArguments(Arguments.proxyPlayerArgument("player"))
//                .withArguments(Arguments.serverArgument("server"))
//                .executes((sender, args) -> {
//                    Profile profile = Profile.getByUsername((String) args[0]);
//                    Server server = (Server) args[1];
//
//                    if (profile == null || !profile.isLoaded()) {
//                        sender.sendMessage(CC.RED + Config.COULD_NOT_RESOLVE_PLAYER.format());
//                        return;
//                    }
//
//                    new ServerMessagePlayerPayload(profile.getName(), "&6You are being sent to " + server.getServerName() + ".").send();
//
//                    sender.sendMessage(CC.CHAT_BAR);
//                    sender.sendMessage(FlameAPI.getColoredName(profile) + CC.GRAY + " has been sent to " + server.getServerName() + ".");
//                    sender.sendMessage(CC.CHAT_BAR);
//
//                    new ProxyCommandPayload("send", profile.getName(), server.getServerName()).send();
//                });
//    }
//
//}
