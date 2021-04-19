package com.customwrld.flame.profile.grant;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.json.JsonSerializer;
import com.customwrld.flame.rank.Rank;

import java.util.HashMap;
import java.util.Map;

public class GrantJsonSerializer implements JsonSerializer<Grant> {

    @Override
    public String serialize(Grant grant) {
        Map<String, String> object = new HashMap<>();
        object.put("uuid", grant.getUuid().toString());
        object.put("rank", Rank.SERIALIZER.serialize(grant.getRank()));
        object.put("addedBy", grant.getAddedBy() == null ? null : grant.getAddedBy().toString());
        object.put("addedAt", String.valueOf(grant.getAddedAt()));
        object.put("addedReason", grant.getAddedReason());
        object.put("duration", String.valueOf(grant.getDuration()));
        object.put("removedBy", grant.getRemovedBy() == null ? null : grant.getRemovedBy().toString());
        object.put("removedAt", String.valueOf(grant.getRemovedAt()));
        object.put("removedReason", grant.getRemovedReason());
        object.put("removed", String.valueOf(grant.isRemoved()));
        return CustomLib.GSON.toJson(object);
    }

}