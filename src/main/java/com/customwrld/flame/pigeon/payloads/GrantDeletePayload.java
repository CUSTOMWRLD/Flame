package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GrantDeletePayload extends Payload {

    public UUID recipient;
    public Grant grant;

    public GrantDeletePayload() {
        this.name = "grant-delete";
    }

    public GrantDeletePayload(UUID recipient, Grant grant) {
        this();
        this.recipient = recipient;
        this.grant = grant;
    }

    @Override
    public void receive(Payload received) {
        GrantDeletePayload payload = (GrantDeletePayload) received;

        Player player = Bukkit.getPlayer(payload.recipient);
        Grant grant = payload.grant;

        if (player != null) {
            Profile profile = Profile.getProfiles().get(player.getUniqueId());
            profile.getGrants().removeIf(other -> Objects.equals(other, grant));
            profile.getGrants().add(grant);
            if (grant.hasExpired()) {
                Grant.expireGrant(player, grant);
            } else {
                Grant.removeGrant(player, grant);
            }

        }

    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("recipient", recipient.toString());
        map.put("grant", Grant.SERIALIZER.serialize(grant));
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> fromJson = CustomLib.GSON.fromJson(json, type);

        UUID recipient = UUID.fromString(fromJson.get("recipient"));
        Grant grant = Grant.DESERIALIZER.deserialize(fromJson.get("grant"));

        return new GrantDeletePayload(recipient, grant);
    }

}
