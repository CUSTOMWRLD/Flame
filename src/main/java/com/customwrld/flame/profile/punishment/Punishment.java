package com.customwrld.flame.profile.punishment;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.DiscordWebhook;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.pigeon.payloads.discord.PunishmentEmbedPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.pigeon.Pigeon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.*;
import java.util.List;

@Getter @Setter
public class Punishment {

    public static PunishmentJsonSerializer SERIALIZER = new PunishmentJsonSerializer();
    public static PunishmentJsonDeserializer DESERIALIZER = new PunishmentJsonDeserializer();

    private final UUID uuid;
    private final PunishmentType type;
    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;
    private final long duration;
    private UUID removedBy;
    private long removedAt;
    private String removedReason;
    private boolean removed;

    public Punishment(UUID uuid, PunishmentType type, UUID addedBy, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.type = type;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public static Punishment fromSimple(Map<String, String> simple) {
        Punishment punishment = new Punishment(UUID.fromString(simple.get("uuid")),
                PunishmentType.valueOf(simple.get("punishmentType")),
                !simple.get("addedBy").equals("null") ? UUID.fromString(simple.get("addedBy")) : null,
                Long.parseLong(simple.get("addedAt")),
                simple.get("addedReason"),
                Long.parseLong(simple.get("duration"))
        );

        String isRemoved = simple.get("removed");
        if (isRemoved != null && isRemoved.equals("true")) {
            punishment.setRemoved(true);
            punishment.setRemovedAt(Long.parseLong(simple.get("removedAt")));
            punishment.setRemovedBy(!simple.get("removedBy").equals("null") ? UUID.fromString(simple.get("removedBy")) : null);
            punishment.setRemovedReason(simple.get("removedReason"));
        }

        return punishment;
    }

    public boolean isPermanent() {
        return type == PunishmentType.BLACKLIST || duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !this.removed && (this.isPermanent() || this.getMillisRemaining() < 0L);
    }

    public long getMillisRemaining() {
        return (addedAt + duration) - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return (!isPermanent()) && (System.currentTimeMillis() >= addedAt + duration);
    }

    public String getDurationText() {
        if (isPermanent() || duration == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(duration);
        }
    }

    public String getTimeRemaining() {
        if (removed) {
            return "Removed";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        if (hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((addedAt + duration) - System.currentTimeMillis());
    }

    public String getContext() {
        if (!(type == PunishmentType.BAN || type == PunishmentType.MUTE)) {
            return removed ? type.getUndoContext() : type.getContext();
        }

        if (isPermanent()) {
            return (removed ? type.getUndoContext() : "permanently " + type.getContext());
        } else {
            return (removed ? type.getUndoContext() : "temporarily " + type.getContext());
        }
    }

    public void broadcast(Boolean silent, Profile profile) {
        if (profile == null || !profile.isLoaded()) {
            return;
        }

        String sender = CC.DARK_RED + "Console";

        if (getAddedBy() != null) {
            sender = FlameAPI.getColoredName(Profile.getByUuid(getAddedBy()));
        }

        String punishmentMessage = FlameAPI.getColoredName(profile) + CC.GREEN + " has been " + getContext() + " by " + CC.RESET + sender;

        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    if (silent) {
                        if (player.hasPermission("flame.staff")) {
                            player.sendMessage(CC.GRAY + "[Silent] " + CC.RESET + punishmentMessage);
                        }
                    } else {
                        player.sendMessage(punishmentMessage);
                    }
                });
    }

    public List<String> getMuteMessage() {
        if(this.type != PunishmentType.MUTE) {
            return null;
        } else {
            return Arrays.asList(
                    CC.CHAT_BAR,
                    CC.RED + "You have been muted for: " + this.getAddedReason(),
                    "",
                    (this.isPermanent() ? "&7You are permanently muted." : "&7Your mute will expire in &c" + this.getTimeRemaining()),
                    CC.CHAT_BAR
            );
        }
    }

    public String getKickMessage() {
        String punishmentBar = "&9&m        &b&m        &f&m        &r &8[&3&l" + this.type.getReadable().toUpperCase() + "&8] &f&m        &b&m        &9&m        ";

        Date date = new Date(this.getAddedAt());

        return CC.translate(punishmentBar +
                "\n\n&9Your account has been " + this.getType().getContext() + " from the CUSTOMWRLD Network.\n" +
                "\n&fReason: &9" + this.addedReason +
                "\n&fAdded On: &9" + TimeUtil.dateToString(date) + " &fat &9" + TimeUtil.dateToTime(date) +
                (!this.isPermanent() ? "\n&fTime Remaining: &9" + this.getTimeRemaining() : "") +
                "\n\n" + punishmentBar);
    }

    public void sendEmbed(UUID player) {
        CustomLib.getCustomLib().getPigeon().broadcast(new PunishmentEmbedPayload(player, this));

    }

    public HashMap<String, String> simplify() {
        HashMap<String, String> simple = new HashMap<>();
        simple.put("uuid", uuid.toString());
        simple.put("punishmentType", type.toString());
        simple.put("addedAt", String.valueOf(addedAt));
        simple.put("addedBy", addedBy != null ? addedBy.toString() : "null");
        simple.put("addedReason", addedReason);
        simple.put("duration", String.valueOf(duration));

        if (removed) {
            simple.put("removed", "true");
            simple.put("removedAt", String.valueOf(removedAt));
            simple.put("removedBy", removedBy != null ? removedBy.toString() : "null");
            simple.put("removedReason", removedReason);
        }

        return simple;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Punishment && ((Punishment) object).uuid.equals(uuid);
    }

}