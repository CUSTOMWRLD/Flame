package com.customwrld.flame.rank;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RankJsonDeserializer implements JsonDeserializer<Rank> {

    @Override
    public Rank deserialize(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> object = CustomLib.GSON.fromJson(json, type);

        Rank rank = new Rank(
                UUID.fromString(object.get("uuid")),
                object.get("name"),
                ChatColor.of(object.get("color")),
                Integer.parseInt(object.get("weight")),
                Boolean.parseBoolean(object.get("defaultRank")));

        rank.getPermissions().clear();
        rank.getPermissions().addAll(CustomLib.GSON.<List<String>>fromJson(object.get("permissions"), CustomLib.LIST_STRING_TYPE));

        rank.getInherited().clear();

        CustomLib.GSON.<List<String>>fromJson(object.get("inherits"), CustomLib.LIST_STRING_TYPE)
                .stream()
                .map(s -> Rank.getRankByUuid(UUID.fromString(s)))
                .filter(Objects::nonNull)
                .forEach(rank.getInherited()::add);

        return rank;
    }

}