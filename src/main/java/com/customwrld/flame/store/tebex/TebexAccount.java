package com.customwrld.flame.store.tebex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class TebexAccount {
    private String id;
    private String domain;
    private String name;
    private String currency;
    private String currencySymbol;
    private boolean onlineMode;
    private String gameType;
    private boolean logEvents;

    public TebexAccount(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("id")) {
                id = obj.get("id").getAsString();
            }
            if (obj.has("domain")) {
                domain = obj.get("domain").getAsString();
            }
            if (obj.has("name")) {
                name = obj.get("name").getAsString();
            }
            if (obj.has("currency")) {
                JsonObject currencyInfo = obj.getAsJsonObject("currency");
                if (currencyInfo.has("iso_4217")) {
                    currency = currencyInfo.get("iso_4217").getAsString();
                }
                if (currencyInfo.has("symbol")) {
                    currencySymbol = currencyInfo.get("symbol").getAsString();
                }
            }
            if (obj.has("online_mode")) {
                onlineMode = obj.get("online_mode").getAsBoolean();
            }
            if (obj.has("game_type")) {
                gameType = obj.get("game_type").getAsString();
            }
            if (obj.has("log_events")) {
                logEvents = obj.get("log_events").getAsBoolean();
            }
        }
    }
}
