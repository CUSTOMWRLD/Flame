package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.pigeon.Pigeon;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerMessagePlayerPayload extends Payload {

    public String player;
    public String message;

    public ServerMessagePlayerPayload() {
        this.name = "message-player";
    }

    public ServerMessagePlayerPayload(String player, String message) {
        this();
        this.player = player;
        this.message = message;
    }

    @Override
    public void receive(Payload received) {
        ServerMessagePlayerPayload payload = (ServerMessagePlayerPayload) received;

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getName().equalsIgnoreCase(payload.player)) {
                player.sendMessage(CC.translate(payload.message));
            }
        });
    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("player", player);
        map.put("message", message.getBytes(StandardCharsets.UTF_16));
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        String player = (String) fromJson.get("player");
        String message = Pigeon.decodeString((ArrayList<Double>) fromJson.get("message"));

        return new ServerMessagePlayerPayload(player, message);
    }

}
