package com.customwrld.flame.profile.punishment;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class PunishmentJsonDeserializer implements JsonDeserializer<Punishment> {

    @Override
    public Punishment deserialize(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> object = CustomLib.GSON.fromJson(json, type);

        Punishment punishment = new Punishment(
                UUID.fromString(object.get("uuid")),
                PunishmentType.valueOf(object.get("type")),
                object.get("addedBy") != null ? UUID.fromString(object.get("addedBy")) : null,
                Long.parseLong(object.get("addedAt")),
                object.get("addedReason"),
                Long.parseLong(object.get("duration"))
        );

        if (object.get("removedBy") != null && !object.get("removedBy").isEmpty()) {
            punishment.setRemovedBy(UUID.fromString(object.get("removedBy")));
        }

        if (object.get("removedAt") != null && !object.get("removedAt").isEmpty()) {
            punishment.setRemovedAt(Long.parseLong(object.get("removedAt")));
        }

        if (object.get("removedReason") != null && !object.get("removedReason").isEmpty()) {
            punishment.setRemovedReason(object.get("removedReason"));
        }

        if (object.get("removed") != null && !object.get("removed").isEmpty()) {
            punishment.setRemoved(Boolean.parseBoolean(object.get("removed")));
        }

        return punishment;
    }

}
