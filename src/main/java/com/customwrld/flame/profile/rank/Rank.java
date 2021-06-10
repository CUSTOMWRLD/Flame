package com.customwrld.flame.profile.rank;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.Profile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Rank {

    @Getter
    private static final Map<UUID, Rank> ranks = new HashMap<>();
    public static Comparator<Rank> COMPARATOR = (rank1, rank2) -> rank2.getWeight() - rank1.getWeight();
    @Getter
    private static MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("ranks");
    private final UUID uuid;
    private final String name;
    private final List<Rank> inherited = new ArrayList<>();
    private final List<String> permissions = new ArrayList<>();
    private ChatColor color;
    private String prefix;
    private int weight;
    private boolean defaultRank;

    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.color = ChatColor.WHITE;
        this.weight = 0;
        this.defaultRank = false;

        ranks.put(uuid, this);
    }

    public Rank(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        ranks.put(uuid, this);
    }

    public Rank(UUID uuid, String name, ChatColor color, int weight, boolean defaultRank) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.weight = weight;
        this.defaultRank = defaultRank;

        ranks.put(uuid, this);
    }

    public static Rank findByName(String name) {
        return ranks.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void init() {
        Map<Rank, List<UUID>> inheritanceReferences = new HashMap<>();

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Rank rank = new Rank(UUID.fromString(document.getString("uuid")), document.getString("name"));
                rank.load(document);
                rank.inherited.clear();

                List<UUID> ranksToInherit = new ArrayList<>();

                CustomLib.getCustomLib().getGson().<List<String>>fromJson(document.getString("inherits"), ArrayList.class)
                        .stream()
                        .map(UUID::fromString)
                        .forEach(ranksToInherit::add);

                inheritanceReferences.put(rank, ranksToInherit);

                ranks.put(rank.getUuid(), rank);
            }
        }

        inheritanceReferences.forEach((rank, list) -> list.forEach(uuid -> {
            Rank inherited = ranks.get(uuid);

            if (inherited != null) {
                rank.getInherited().add(inherited);
            }
        }));

        getDefaultRank();
    }

    public static Rank getRankByUuid(UUID uuid) {
        return ranks.get(uuid);
    }

    public static Rank getRankByName(String name) {
        for (Rank rank : ranks.values()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public static Rank getDefaultRank() {
        for (Rank rank : ranks.values()) {
            if (rank.isDefaultRank()) {
                return rank;
            }
        }

        Rank defaultRank = new Rank("Default");
        defaultRank.setDefaultRank(true);
        defaultRank.save();

        ranks.put(defaultRank.getUuid(), defaultRank);

        return defaultRank;
    }

    public boolean isDefaultRank() {
        return defaultRank;
    }

    public boolean hasPermission(String permission) {
        return hasPermission(permission, true);
    }

    public boolean hasPermission(String permission, boolean includeInheritance) {
        if (permissions.contains(permission)) {
            return true;
        }

        if (includeInheritance) {
            for (Rank rank : inherited) {
                if (rank.hasPermission(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canInherit(Rank rankToCheck) {
        if (this == rankToCheck || inherited.contains(rankToCheck) || rankToCheck.inherited.contains(this)) {
            return false;
        }

        for (Rank rank : inherited) {
            if (!rank.canInherit(rankToCheck)) {
                return false;
            }
        }

        return true;
    }

    public List<String> getAllPermissions() {
        return getAllPermissions(true);
    }

    public List<String> getAllPermissions(boolean includeInheritance) {
        List<String> permissions = new ArrayList<>(this.permissions);

        if (includeInheritance) {
            for (Rank rank : inherited) {
                permissions.addAll(rank.getAllPermissions());
            }
        }

        return permissions;
    }

    public void load() {
        load(collection.find(Filters.eq("uuid", uuid.toString())).first());
    }

    private void load(Document document) {
        if (document == null) {
            return;
        }

        color = ChatColor.valueOf(document.getString("color"));
        prefix = document.getString("prefix");
        weight = document.getInteger("weight");
        defaultRank = document.getBoolean("defaultRank");

        permissions.clear();
        permissions.addAll(CustomLib.getCustomLib().getGson().<List<String>>fromJson(document.getString("permissions"), ArrayList.class));

        inherited.clear();

        CustomLib.getCustomLib().getGson().<List<String>>fromJson(document.getString("inherits"), ArrayList.class)
                .stream()
                .map(s -> Rank.getRankByUuid(UUID.fromString(s)))
                .filter(Objects::nonNull)
                .forEach(inherited::add);
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        document.put("color", color.name());
        document.put("prefix", prefix);
        document.put("weight", weight);
        document.put("defaultRank", defaultRank);
        document.put("permissions", CustomLib.getCustomLib().getGson().toJson(permissions));
        document.put("inherits", CustomLib.getCustomLib().getGson().toJson(inherited
                .stream()
                .map(Rank::getUuid)
                .map(UUID::toString)
                .collect(Collectors.toList())));

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document,
                new ReplaceOptions().upsert(true));
    }

    public void delete() {
        ranks.remove(uuid);
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Flame.getFlame(), () -> collection.deleteOne(Filters.eq("uuid", uuid.toString())), (20 * 5));
    }

}