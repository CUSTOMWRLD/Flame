package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.network.ServerType;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.pigeon.Pigeon;
import com.customwrld.flame.network.Server;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateServerInfoPayload extends Payload {

    public ServerType serverType;
    public String serverName;
    public String playerList;
    public double tps;
    public int online;
    public int max;
    public long uptime;

    public UpdateServerInfoPayload() {
        this.name = "update-server-info";
    }

    public UpdateServerInfoPayload(ServerType serverType, String serverName, String playerList, long uptime, double tps, int online, int max) {
        this();
        this.serverType = serverType;
        this.serverName = serverName;
        this.playerList = playerList;
        this.uptime = uptime;
        this.tps = tps;
        this.online = online;
        this.max = max;
    }

    @Override
    public void receive(Payload received) {
        UpdateServerInfoPayload payload = (UpdateServerInfoPayload) received;

        Server.getServers().put(payload.serverName, new Server(payload));
    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("serverType", serverType.getDisplay());
        map.put("serverName", serverName);
        map.put("playerList", playerList.getBytes(StandardCharsets.UTF_16));
        map.put("uptime", uptime);
        map.put("tps", tps);
        map.put("online", online);
        map.put("max", max);
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        ServerType serverType = ServerType.valueOf((String) fromJson.get("serverType"));
        String serverName = (String) fromJson.get("serverName");
        String playerList = Pigeon.decodeString((ArrayList<Double>) fromJson.get("playerList"));
        long uptime = ((Double) fromJson.get("uptime")).longValue();
        double tps = (double) fromJson.get("tps");
        int online = ((Double) fromJson.get("online")).intValue();
        int max = ((Double) fromJson.get("max")).intValue();

        return new UpdateServerInfoPayload(serverType, serverName, playerList, uptime, tps, online, max);
    }
}
