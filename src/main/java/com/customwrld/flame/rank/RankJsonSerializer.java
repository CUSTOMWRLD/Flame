package com.customwrld.flame.rank;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankJsonSerializer implements JsonSerializer<Rank> {

    @Override
    public String serialize(Rank rank) {
        Map<String, String> object = new HashMap<>();
        object.put("uuid", rank.getUuid().toString());
        object.put("name", rank.getName());
        object.put("defaultRank", String.valueOf(rank.isDefaultRank()));
        object.put("color", rank.getColor().getName());
        object.put("weight", String.valueOf(rank.getWeight()));
        object.put("permissions", CustomLib.GSON.toJson(rank.getAllPermissions()));
        object.put("inherits", CustomLib.GSON.toJson(rank.getInherited()
                .stream()
                .map(Rank::getUuid)
                .map(UUID::toString)
                .collect(Collectors.toList())));
        return CustomLib.GSON.toJson(object);
    }

}