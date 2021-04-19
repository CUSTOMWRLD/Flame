package com.customwrld.flame.profile.grant;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonDeserializer;
import com.customwrld.flame.rank.Rank;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class GrantJsonDeserializer implements JsonDeserializer<Grant> {

    @Override
    public Grant deserialize(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> object = CustomLib.GSON.fromJson(json, type);

        Grant grant = new Grant(
                UUID.fromString(object.get("uuid")),
                Rank.DESERIALIZER.deserialize(object.get("rank")),
                null,
                Long.parseLong(object.get("addedAt")),
                object.get("addedReason"),
                Long.parseLong(object.get("duration"))
        );

        if (object.get("addedBy") != null) {
            grant.setAddedBy(UUID.fromString(object.get("addedBy")));
        }

        if (object.get("removedBy") != null) {
            grant.setRemovedBy(UUID.fromString(object.get("removedBy")));
        }

        if (object.get("removedAt") != null) {
            grant.setRemovedAt(Long.parseLong(object.get("removedAt")));
        }

        if (object.get("removedReason") != null) {
            grant.setRemovedReason(object.get("removedReason"));
        }

        if (object.get("removed") != null) {
            grant.setRemoved(Boolean.parseBoolean(object.get("removed")));
        }

        return grant;
    }

}