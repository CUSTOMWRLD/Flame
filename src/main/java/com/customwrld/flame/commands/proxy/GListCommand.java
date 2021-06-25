//package com.customwrld.flame.commands.bungeecord;
//
//import com.customwrld.customlib.util.MathUtil;
//import com.customwrld.flame.network.Server;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import org.bukkit.ChatColor;
//
//import java.util.Map;
//
//public class GListCommand extends Command {
//
//    public GListCommand() {
//        this.command = "glist";
//        this.permission = "flame.glist";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withOptionalArgument(Arguments.serverArgument("server"), () -> null)
//                .executes((sender, args) -> {
//                    if (args.length == 0 || args[0] == null) {
//                        sender.sendMessage(CC.CHAT_BAR);
//                        sender.sendMessage(CC.YELLOW + "List of all servers on the Network:");
//                        sender.sendMessage("");
//
//                        for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
//                            Server server = ent.getValue();
//
//                            int percent = server.getOnline() * 100 / server.getMax();
//                            double round = Math.round(percent * 10.0) / 10.0;
//
//                            sender.sendMessage(CC.translate("&6" + ent.getKey() + " &e(" + server.getOnline() + "/" + server.getMax() + ") &8[" + MathUtil.getProgressBar(server.getOnline(), server.getMax(), 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "&8] [&7" + round + "%&8]"));
//                        }
//
//                        sender.sendMessage("");
//                        sender.sendMessage(CC.GOLD + "Total online players: " + CC.GRAY + Server.globalPlayerCount());
//                    } else {
//                        Server server = (Server) args[0];
//
//                        sender.sendMessage(CC.CHAT_BAR);
//                        sender.sendMessage(CC.GOLD + "Server Name: " + CC.GRAY + server.getServerName());
//                        sender.sendMessage(CC.GOLD + "Players: " + CC.GRAY + server.getOnline() + "/" + server.getMax());
//                        sender.sendMessage("");
//                        sender.sendMessage(CC.GRAY + server.getPlayerList());
//                        sender.sendMessage("");
//                    }
//                    sender.sendMessage(CC.CHAT_BAR);
//                });
//    }
//
//}
