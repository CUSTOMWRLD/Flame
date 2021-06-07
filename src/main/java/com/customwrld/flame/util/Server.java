package com.customwrld.flame.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Server {

    public static List<Server> serverList = new ArrayList<>();

    private final String name;
    private final Server.Type type;
    private final Long uptime;
    private final Double tps;
    private final List<String> players;
    private final int maxPlayers;

    public Server(String name, Server.Type type, Long uptime, Double tps, List<String> players, int maxPlayers) {
        this.name = name;
        this.type = type;
        this.uptime = uptime;
        this.tps = tps;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        LOBBY("Lobbies", "lobby"),
        SURVIVAL("Survival", "survival"),
        SKYBLOCK("SkyBlock", "skyblock"),
        PRACTICE("Practice", "practice"),
        SKYWARS("SkyWars", "sw"),
        WOOLWARS("WoolWars", "ww"),
        BEDWARS("BedWars", "bw");

        private final String display;
        private final String prefix;
    }

}
