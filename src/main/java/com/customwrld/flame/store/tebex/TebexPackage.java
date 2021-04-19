package com.customwrld.flame.store.tebex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TebexPackage {
    private String id;
    private String name;
    private String image;
    private String price;
    private String expiryLength;
    private String expiryPeriod;
    private String type;
    private String globalLimit;
    private String globalLimitPeriod;
    private String userLimit;
    private String userLimitPeriod;
    private List<TebexServer> servers;
    private List<TebexPackage> requiredPackages;
    private boolean requireAny;
    private boolean createGiftcard;
    private String showUntil;
    private String guiItem;
    private boolean disabled;
    private boolean disableQuantity;
    private boolean customPrice;
    private boolean chooseServer;
    private boolean limitExpires;
    private boolean inheritCommands;
    private boolean variableGiftcard;
    private String description;

    public TebexPackage(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("id")) {
                id = obj.get("id").getAsString();
            }
            if (obj.has("name")) {
                name = obj.get("name").getAsString();
            }
            if (obj.has("image")) {
                image = obj.get("image").getAsString();
            }
            if (obj.has("price")) {
                price = obj.get("price").getAsString();
            }
            if (obj.has("expiry_length")) {
                expiryLength = obj.get("expiry_length").getAsString();
            }
            if (obj.has("expiry_period")) {
                expiryPeriod = obj.get("expiry_period").getAsString();
            }
            if (obj.has("type")) {
                type = obj.get("type").getAsString();
            }
            if (obj.has("global_limit")) {
                globalLimit = obj.get("global_limit").getAsString();
            }
            if (obj.has("global_limit_period")) {
                globalLimitPeriod = obj.get("global_limit_period").getAsString();
            }
            if (obj.has("user_limit")) {
                userLimit = obj.get("user_limit").getAsString();
            }
            if (obj.has("user_limit_period")) {
                userLimitPeriod = obj.get("user_limit_period").getAsString();
            }
            if (obj.has("servers")) {
                servers = new ArrayList<>();
                for (JsonElement element : obj.get("servers").getAsJsonArray()) {
                    servers.add(new TebexServer(element));
                }
            }
            if (obj.has("required_packages")) {
                requiredPackages = new ArrayList<>();
                for (JsonElement element : obj.get("required_packages").getAsJsonArray()) {
                    requiredPackages.add(new TebexPackage(element));
                }
            }
            if (obj.has("require_any")) {
                requireAny = obj.get("require_any").getAsBoolean();
            }
            if (obj.has("create_giftcard")) {
                createGiftcard = obj.get("create_giftcard").getAsBoolean();
            }
            if (obj.has("show_until")) {
                showUntil = obj.get("show_until").getAsString();
            }
            if (obj.has("gui_item")) {
                guiItem = obj.get("gui_item").getAsString();
            }
            if (obj.has("disabled")) {
                disabled = obj.get("disabled").getAsBoolean();
            }
            if (obj.has("disable_quantity")) {
                disableQuantity = obj.get("disable_quantity").getAsBoolean();
            }
            if (obj.has("custom_price")) {
                customPrice = obj.get("custom_price").getAsBoolean();
            }
            if (obj.has("choose_server")) {
                chooseServer = obj.get("choose_server").getAsBoolean();
            }
            if (obj.has("limit_expires")) {
                limitExpires = obj.get("limit_expires").getAsBoolean();
            }
            if (obj.has("inherit_commands")) {
                inheritCommands = obj.get("inherit_commands").getAsBoolean();
            }
            if (obj.has("variable_giftcard")) {
                variableGiftcard = obj.get("variable_giftcard").getAsBoolean();
            }
            if (obj.has("description")) {
                description = obj.get("description").getAsString();
            }
        }
    }
}