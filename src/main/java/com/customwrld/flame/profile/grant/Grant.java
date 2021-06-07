package com.customwrld.flame.profile.grant;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.rank.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Grant {

    public static GrantJsonSerializer SERIALIZER = new GrantJsonSerializer();
    public static GrantJsonDeserializer DESERIALIZER = new GrantJsonDeserializer();
    public static Comparator<Grant> COMPARATOR = (rank1, rank2) -> rank2.getRank().getWeight() - rank1.getRank().getWeight();

    private final UUID uuid;
    private UUID rank;
    private UUID addedBy;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedBy;
    private long removedAt;
    private String removedReason;
    private boolean removed;

    public Grant(UUID uuid, UUID rank, UUID addedBy, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.rank = rank;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public Rank getRank() {
        return Rank.ranks.get(this.rank);
    }

    public static void loadNewGrant(Profile profile, Player player, Grant grant) {
        if (player != null && grant.getRank() != null) {
            player.sendMessage(CC.GREEN +
                    "A '" + grant.getRank().getName() +
                    "' grant has been applied to you " +
                    (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".");
            if (profile != null) {
//                AccountSync.addGrant(profile, grant);
                profile.reload();
            }
        }
    }

    public static void removeGrant(Player player, Grant grant) {
        if (player != null) {
            player.sendMessage(CC.RED + "Your '" + grant.getRank().getName() + "' grant has been removed.");

            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null) {
//                AccountSync.removeGrant(profile, grant);
                profile.reload();
            }
        }
    }

    public static void expireGrant(Player player, Grant grant) {
        if (player != null) {
            player.sendMessage(CC.RED + ("Your '" + grant.getRank().getName() + "' grant has expired."));

            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null) {
//                AccountSync.removeGrant(profile, grant);
                profile.reload();
            }
        }
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

    public HashMap<String, String> simplify() {
        HashMap<String, String> simple = new HashMap<>();
        simple.put("uuid", uuid.toString());
        simple.put("rank", rank.toString());
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

    public static Grant fromSimple(Map<String, String> simple) {
        Grant grant = new Grant(UUID.fromString(simple.get("uuid")),
                UUID.fromString(simple.get("rank")),
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

    @Override
    public boolean equals(Object object) {
        return object instanceof Grant && ((Grant) object).uuid.equals(uuid);
    }

}