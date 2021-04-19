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

public class BroadcastMessagePayload extends Payload {

    public String message;

    public BroadcastMessagePayload() {
        this.name = "broadcast-message";
    }

    public BroadcastMessagePayload(String message) {
        this();
        this.message = message;
    }

    @Override
    public void receive(Payload received) {
        BroadcastMessagePayload payload = (BroadcastMessagePayload) received;

        Bukkit.broadcastMessage(CC.translate(payload.message));
    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message.getBytes(StandardCharsets.UTF_16));
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        String message = Pigeon.decodeString((ArrayList<Double>) fromJson.get("message"));

        return new BroadcastMessagePayload(message);
    }

}
