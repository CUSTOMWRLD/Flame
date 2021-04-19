package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.rank.UpdateType;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankUpdatePayload extends Payload {

    public UpdateType updateType;
    public String rank;
    public UUID uuid;

    public RankUpdatePayload() {
        this.name = "rank-update";
    }

    public RankUpdatePayload(UpdateType updateType, String rank, UUID uuid) {
        this();
        this.updateType = updateType;
        this.rank = rank;
        this.uuid = uuid;
    }

    @Override
    public void receive(Payload received) {
        RankUpdatePayload payload = (RankUpdatePayload) received;
        Rank rank = Rank.DESERIALIZER.deserialize(payload.rank);

        CC.broadcastOps(payload.updateType.format(rank.getName()));

        Map<UUID, Rank> rankMap = Rank.getRanks();

        switch (payload.updateType) {
            case DELETED -> rankMap.remove(rank.getUuid());
            case CREATED, STAFF_UPDATED, WEIGHT_UPDATED, COLOR_UPDATED, INHERIT_ADDED, INHERIT_REMOVED, PERMISSION_ADDED, PERMISSION_REMOVED -> rankMap.put(rank.getUuid(), rank);
        }

        rank.refreshPlayers();
    }

    @Override
    public void send() {
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("updateType", updateType.toString());
        map.put("rank", rank);
        map.put("uuid", uuid);
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        UpdateType updateType = UpdateType.valueOf((String) fromJson.get("updateType"));
        String rank = (String) fromJson.get("rank");
        UUID uuid = UUID.fromString((String) fromJson.get("uuid"));

        return new RankUpdatePayload(updateType, rank, uuid);
    }

}
