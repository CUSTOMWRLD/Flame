package com.customwrld.flame.profile.grant;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.sync.AccountSync;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Grant {

    public static GrantJsonSerializer SERIALIZER = new GrantJsonSerializer();
    public static GrantJsonDeserializer DESERIALIZER = new GrantJsonDeserializer();

    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private Rank rank;
    @Getter
    @Setter
    private UUID addedBy;
    @Getter
    @Setter
    private long addedAt;
    @Getter
    @Setter
    private String addedReason;
    @Getter
    @Setter
    private long duration;
    @Getter
    @Setter
    private UUID removedBy;
    @Getter
    @Setter
    private long removedAt;
    @Getter
    @Setter
    private String removedReason;
    @Getter
    @Setter
    private boolean removed;

    public Grant(UUID uuid, Rank rank, UUID addedBy, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.rank = rank;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public boolean isPermanent() {
        return duration == Integer.MAX_VALUE;
    }

    public boolean hasExpired() {
        return (!isPermanent()) && (System.currentTimeMillis() >= addedAt + duration);
    }

    public String getExpiresAtDate() {
        if (duration == Integer.MAX_VALUE) {
            return "Never";
        }

        return TimeUtil.dateToString(new Date(addedAt + duration));
    }

    public String getDurationText() {
        if (removed) {
            return "Removed";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        return TimeUtil.millisToRoundedTime(duration);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Grant && ((Grant) object).uuid.equals(uuid);
    }

    public static void loadNewGrant(Profile profile, Player player, Grant grant) {
        if (player != null) {
            player.sendMessage(CC.GREEN +
                    "A '" + grant.getRank().getName() +
                    "' grant has been applied to you " +
                    (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".");
            if (profile != null) {
                AccountSync.addGrant(profile, grant);
                profile.recalculatePlayer(player);
            }
        }
    }

    public static void removeGrant(Player player, Grant grant) {
        if (player != null) {
            player.sendMessage(CC.RED + "Your '" + grant.getRank().getName() + "' grant has been removed.");

            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null) {
                AccountSync.removeGrant(profile, grant);
                profile.recalculatePlayer(player);
            }
        }
    }

    public static void expireGrant(Player player, Grant grant) {
        if (player != null) {
            player.sendMessage(CC.RED + ("Your '{rank}' grant has expired.")
                    .replace("{rank}", grant.getRank().getName()));

            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null) {
                AccountSync.removeGrant(profile, grant);
                profile.recalculatePlayer(player);
            }
        }
    }

    public static Grant fromSimple(Map<String, String> simple) {
        Grant grant = new Grant(UUID.fromString(simple.get("uuid")),
                (!simple.get("rank").equals("null") ? Rank.getRankByName(simple.get("rank")) : null),
                (!simple.get("addedBy").equals("null") ? UUID.fromString(simple.get("addedBy")) : null),
                Long.parseLong(simple.get("addedAt")),
                simple.get("addedReason"),
                Long.parseLong(simple.get("duration"))
        );

        String isRemoved = simple.get("removed");
        if (isRemoved != null && isRemoved.equals("true")) {
            grant.setRemoved(true);
            grant.setRemovedAt(Long.parseLong(simple.get("removedAt")));
            grant.setRemovedBy(!simple.get("removedBy").equals("null") ? UUID.fromString(simple.get("removedBy")) : null);
            grant.setRemovedReason(simple.get("removedReason"));
        }

        return grant;
    }

    public HashMap<String, String> simplify() {
        HashMap<String, String> simple = new HashMap<>();
        simple.put("uuid", uuid.toString());
        simple.put("rank", rank != null ? rank.getName() : "null");
        simple.put("addedBy", addedBy != null ? addedBy.toString() : "null");
        simple.put("addedAt", String.valueOf(addedAt));
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

}