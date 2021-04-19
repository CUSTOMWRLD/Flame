package com.customwrld.flame.pigeon.payloads;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.Config;
import com.customwrld.flame.chat.StaffChatEnum;
import com.customwrld.flame.pigeon.Payload;
import com.customwrld.flame.pigeon.Pigeon;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.StaffProfile;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StaffChatPayload extends Payload {

    public StaffChatEnum staffChatEnum;
    public String server;
    public String staff;
    public String message;

    public StaffChatPayload() {
        this.name = "staff-chat";
    }

    public StaffChatPayload(StaffChatEnum staffChatEnum, String server, String staff, String message) {
        this();
        this.staffChatEnum = staffChatEnum;
        this.server = server;
        this.staff = staff;
        this.message = message;
    }

    @Override
    public void receive(Payload received) {
        StaffChatPayload payload = (StaffChatPayload) received;

        StaffChatEnum staffChatEnum = payload.staffChatEnum;

        switch (staffChatEnum) {
            case STAFF_CHAT -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("flame.staffchat"))
                    .forEach(player -> {
                        StaffProfile profile = Profile.getByUuid(player.getUniqueId()).getStaffProfile();

                        if (profile.staffMessagesEnabled()) {
                            player.sendMessage(Config.STAFF_CHAT.format(payload.server, payload.staff, payload.message));
                        }

                    });
            case ADMIN_CHAT -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("flame.adminchat"))
                    .forEach(player -> {
                        StaffProfile profile = Profile.getByUuid(player.getUniqueId()).getStaffProfile();

                        if (profile.staffMessagesEnabled()) {
                            player.sendMessage(Config.ADMIN_CHAT.format(payload.server, payload.staff, payload.message));
                        }
                    });
        }
    }

    @Override
    public void send() {
        System.out.println(toJson());
        Flame.get().getPigeon().produce(name, toJson());
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("staffChatEnum", staffChatEnum.name());
        map.put("server", server);
        map.put("staff", staff.getBytes(StandardCharsets.UTF_16));
        map.put("message", message.getBytes(StandardCharsets.UTF_16));
        return CustomLib.GSON.toJson(map);
    }

    @Override
    public Payload fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> fromJson = CustomLib.GSON.fromJson(json, type);

        StaffChatEnum staffChatEnum = StaffChatEnum.valueOf((String) fromJson.get("staffChatEnum"));
        String server = (String) fromJson.get("server");
        String staff = Pigeon.decodeString((ArrayList<Double>) fromJson.get("staff"));
        String message = Pigeon.decodeString((ArrayList<Double>) fromJson.get("message"));

        return new StaffChatPayload(staffChatEnum, server, staff, message);
    }

}
