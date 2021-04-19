package com.customwrld.flame.commands.bungeecord;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.MathUtil;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.network.Server;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Map;

public class GListCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("glist")
                .withPermission("flame.glist")
                .withOptionalArgument(Arguments.serverArgument("server"), () -> null)
                .executes((sender, args) -> {
                    if (args.length == 0 || args[0] == null) {
                        sender.sendMessage(CC.CHAT_BAR);
                        sender.sendMessage(CC.YELLOW + "List of all servers on the Network:");
                        sender.sendMessage("");

                        for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
                            Server server = ent.getValue();

                            int percent = server.getOnline() * 100 / server.getMax();
                            double round = Math.round(percent * 10.0) / 10.0;

                            sender.sendMessage(CC.translate("&6" + ent.getKey() + " &e(" + server.getOnline() + "/" + server.getMax() + ") &8[" + MathUtil.getProgressBar(server.getOnline(), server.getMax(), 25, '|', ChatColor.GREEN, ChatColor.GRAY) + "&8] [&7" + round + "%&8]"));
                        }

                        sender.sendMessage("");
                        sender.sendMessage(CC.GOLD + "Total online players: " + CC.GRAY + Server.globalPlayerCount());
                        sender.sendMessage(CC.CHAT_BAR);
                    } else {
                        Server server = (Server) args[0];

                        Arrays.asList(
                                CC.CHAT_BAR,
                                CC.GOLD + "Server Name: " + CC.GRAY + server.getServerName(),
                                CC.GOLD + "Players: " + CC.GRAY + server.getOnline() + "/" + server.getMax(),
                                "",
                                CC.GRAY + server.getPlayerList(),
                                "",
                                CC.CHAT_BAR).forEach(sender::sendMessage);
                    }
                });
    }

}
