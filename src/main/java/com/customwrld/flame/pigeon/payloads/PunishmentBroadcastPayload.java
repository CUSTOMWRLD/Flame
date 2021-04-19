package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.pigeon.Pigeon;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentBroadcastPayload extends Payload {

    public Punishment punishment;
    public UUID offender;
    public Boolean silent;

    public PunishmentBroadcastPayload() {
        this.name = "punishment-broadcast";
    }

    public PunishmentBroadcastPayload(Punishment punishment, UUID offender, Boolean silent) {
        this();
        this.punishment = punishment;
        this.offender = offender;
        this.silent = silent;
    }

    @Override
    public void receive(Payload received) {
        PunishmentBroadcastPayload payload = (PunishmentBroadcastPayload) received;
        payload.punishment.broadcast(payload.silent, Profile.getByUuid(payload.offender));
    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("punishment", Punishment.SERIALIZER.serialize(punishment).getBytes(StandardCharsets.UTF_16));
        map.put("offender", offender.toString());
        map.put("silent", silent.toString());
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        Punishment punishment = Punishment.DESERIALIZER.deserialize(Pigeon.decodeString((ArrayList<Double>) fromJson.get("punishment")));
        UUID offender = UUID.fromString((String) fromJson.get("offender"));
        Boolean silent = Boolean.parseBoolean((String) fromJson.get("silent"));

        return new PunishmentBroadcastPayload(punishment, offender, silent);
    }

}
