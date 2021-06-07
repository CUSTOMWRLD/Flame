package com.customwrld.flame.network;

import com.customwrld.flame.pigeon.payloads.UpdateServerInfoPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class Server {

    private final String serverName;
    private final String playerList;
    private final long lastUpdate;
    private final long uptime;
    private final double tps;
    private final int online;
    private final int max;

    public Server(UpdateServerInfoPayload payload) {
        serverName = payload.serverName;
        playerList = payload.playerList;
        lastUpdate = System.currentTimeMillis();
        uptime = payload.uptime;
        tps = payload.tps;
        online = payload.online;
        max = payload.max;
    }

}
