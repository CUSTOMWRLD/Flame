package com.customwrld.flame.store.tebex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class TebexServer {
    String id;
    String name;

    public TebexServer(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("id")) {
                id = obj.get("id").getAsString();
            }
            if (obj.has("name")) {
                id = obj.get("name").getAsString();
            }
        }
    }
}
