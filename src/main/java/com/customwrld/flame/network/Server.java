package com.customwrld.flame.network;

import com.customwrld.flame.pigeon.payloads.UpdateServerInfoPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class Server {

    @Getter static private Map<String, Server> servers = new HashMap<>();

    private String serverName;
    private String playerList;
    private long lastUpdate;
    private long uptime;
    private double tps;
    private int online;
    private int max;

    public Server(UpdateServerInfoPayload payload) {
        serverName = payload.serverName;
        playerList = payload.playerList;
        lastUpdate = System.currentTimeMillis();
        uptime = payload.uptime;
        tps = payload.tps;
        online = payload.online;
        max = payload.max;
    }

    public static Server getServerByPlayer(String player) {
        for (Map.Entry<String, Server> ent : getServers().entrySet()) {
            Server server = ent.getValue();

            if (server.getPlayerList().toLowerCase().contains(player.toLowerCase())) {
                return server;
            }
        }
        return null;
    }

    public static Server getServerByName(String name) {
        for (Map.Entry<String, Server> ent : getServers().entrySet()) {
            Server server = ent.getValue();

            if (server.getServerName().equalsIgnoreCase(name)) {
                return server;
            }
        }
        return null;
    }

    public static int globalPlayerCount() {
        int count = 0;

        for (Map.Entry<String, Server> ent : getServers().entrySet()) {
            Server server = ent.getValue();

            count += server.getOnline();
        }

        return count;
    }
}
