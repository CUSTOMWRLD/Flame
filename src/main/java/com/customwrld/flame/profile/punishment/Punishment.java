package com.customwrld.flame.profile.punishment;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.profile.Profile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Punishment {

    @Getter
    private static final MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("punishments");

    private final String id;
    private UUID player;
    private Type type;
    private UUID addedBy;
    private Long addedAt;
    private String addedReason;
    private Long duration;
    private UUID removedBy;
    private Long removedAt;
    private String removedReason;
    private boolean removed;

    public Punishment(String id, UUID player, Type type, UUID addedBy, Long addedAt, String addedReason, Long duration) {
        this.id = id;
        this.player = player;
        this.type = type;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public static Punishment getById(String id) {
        Document document = collection.find(Filters.eq("_id", id)).first();

        if (document != null) {
            Punishment punishment = new Punishment(document.getString("_id"),
                    UUID.fromString(document.getString("player")),
                    Type.valueOf(document.getString("type")),
                    document.getString("addedBy") != null ? UUID.fromString(document.getString("addedBy")) : null,
                    document.getLong("addedAt"),
                    document.getString("addedReason"),
                    document.getLong("duration"));

            punishment.setRemoved(document.getBoolean("removed"));

            if (punishment.isRemoved()) {
                punishment.setRemovedAt(document.getLong("removedAt"));
                punishment.setRemovedBy(document.getString("removedBy") != null ? UUID.fromString(document.getString("removedBy")) : null);
                punishment.setRemovedReason(document.getString("removedReason"));
            }

            return punishment;
        }

        return null;
    }

    public boolean isPermanent() {
        return type == Type.BLACKLIST || duration == Integer.MAX_VALUE;
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
        if (!(type == Type.BAN || type == Type.MUTE)) {
            return removed ? type.getUndoContext() : type.getContext();
        }

        if (isPermanent()) {
            return (removed ? type.getUndoContext() : "permanently " + type.getContext());
        } else {
            return (removed ? type.getUndoContext() : "temporarily " + type.getContext());
        }
    }

    public void broadcast(Profile profile) {
        if (profile == null || !profile.isLoaded()) {
            return;
        }

        String sender = "CONSOLE";

        if (getAddedBy() != null) {
            sender = Profile.getByUuid(getAddedBy()).getName();
        }

        String punishmentMessage = CC.DARK_AQUA + CC.BOLD + "Punishments" + CC.DARK_GRAY + CC.BOLD + " » " + CC.AQUA + profile.getName() + CC.GRAY + " has been " + getContext() + " by " + CC.AQUA + sender;

        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    if (player.hasPermission("flame.staff")) {
                        player.sendMessage(CC.CHAT_BAR);
                        player.sendMessage(punishmentMessage);
                        player.sendMessage(CC.CHAT_BAR);
                    }
                });
    }

    public List<String> getPunishmentMessage() {
        Date date = new Date(this.getAddedAt());

        return Arrays.asList(
                CC.CHAT_BAR,
                CC.RED + "You are " + this.getContext() + (!this.isPermanent() ? " for " + this.getTimeRemaining() : "") + ".",
                "",
                CC.GRAY + "Reason: " + CC.WHITE + this.getAddedReason(),
                CC.GRAY + "Added On: " + CC.WHITE + TimeUtil.dateToString(date) + " at " + TimeUtil.dateToTime(date),
                CC.GRAY + "Punishment ID: " + CC.WHITE + this.getId(),
                "",
                CC.GRAY + "Appeal At: " + CC.AQUA + CC.UNDER_LINE + "http://customwrld.com/appeal",
                CC.CHAT_BAR
        );
    }

    public void save() {
        Document document = new Document();
        document.put("_id", this.id);
        document.put("player", this.player.toString());
        document.put("type", this.type.name());
        document.put("addedBy", this.addedBy != null ? this.addedBy.toString() : null);
        document.put("addedAt", this.addedAt);
        document.put("addedReason", this.addedReason);
        document.put("duration", this.duration);

        document.put("removed", this.removed);
        document.put("removedAt", this.removedAt != null ? this.removedAt : null);
        document.put("removedBy", this.removedBy != null ? this.removedBy.toString() : null);
        document.put("removedReason", this.removedReason != null ? this.removedReason : null);

        collection.replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true));
    }

    public void delete() {
        collection.deleteOne(Filters.eq("_id", this.id));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Punishment && ((Punishment) object).id.equals(this.id);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {

        BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true, new Data("Blacklists", ChatColor.DARK_RED, Material.RED_WOOL)),
        BAN("Ban", "banned", "unbanned", true, true, new Data("Bans", ChatColor.GOLD, Material.ORANGE_WOOL)),
        MUTE("Mute", "muted", "unmuted", false, true, new Data("Mutes", ChatColor.GREEN, Material.LIME_WOOL)),
        WARN("Warning", "warned", null, false, false, new Data("Warnings", ChatColor.BLUE, Material.LIGHT_BLUE_WOOL));

        private final String readable;
        private final String context;
        private final String undoContext;
        private final boolean ban;
        private final boolean removable;
        private final Data typeData;

        @Getter
        @AllArgsConstructor
        public static class Data {

            private final String readable;
            private final ChatColor color;
            private final Material material;

        }

    }

}