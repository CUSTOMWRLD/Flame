package com.customwrld.flame.profile.punishment;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class PunishmentJsonSerializer implements JsonSerializer<Punishment> {

    @Override
    public String serialize(Punishment punishment) {
        Map<String, String> object = new HashMap<>();
        object.put("uuid", punishment.getUuid().toString());
        object.put("type", punishment.getType().name());
        object.put("addedBy", punishment.getAddedBy() == null ? null : punishment.getAddedBy().toString());
        object.put("addedAt", String.valueOf(punishment.getAddedAt()));
        object.put("addedReason", punishment.getAddedReason());
        object.put("duration", String.valueOf(punishment.getDuration()));
        object.put("removedBy", punishment.getRemovedBy() == null ? null : punishment.getRemovedBy().toString());
        object.put("removedAt", String.valueOf(punishment.getRemovedAt()));
        object.put("removedReason", punishment.getRemovedReason());
        object.put("removed", String.valueOf(punishment.isRemoved()));
        return CustomLib.getCustomLib().getGson().toJson(object);
    }

}
