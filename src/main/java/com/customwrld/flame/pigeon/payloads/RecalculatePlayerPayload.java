package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.profile.Profile;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecalculatePlayerPayload extends Payload {

    public UUID uuid;

    public RecalculatePlayerPayload() {
        this.name = "recalculate-player";
    }

    public RecalculatePlayerPayload(UUID uuid) {
        this();
        this.uuid = uuid;
    }

    @Override
    public void receive(Payload received) {
        RecalculatePlayerPayload payload = (RecalculatePlayerPayload) received;

        UUID uuid = payload.uuid;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                Profile profile = Profile.getByUuid(uuid);
                profile.recalculatePlayer(player);
            }
        }

    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        UUID uuid = UUID.fromString((String) fromJson.get("uuid"));

        return new RecalculatePlayerPayload(uuid);
    }

}
