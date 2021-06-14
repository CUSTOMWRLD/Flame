package com.customwrld.flame.profile.rank;

import com.customwrld.customlib.CustomLib;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.grant.Grant;;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class Rank {

    @Getter private static final Map<UUID, Rank> ranks = new HashMap<>();
    @Getter private static MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("ranks");

    public static Comparator<Rank> COMPARATOR = (rank1, rank2) -> rank2.getWeight() - rank1.getWeight();

    private final UUID uuid;
    private final String name;
    private final List<Rank> inherited = new ArrayList<>();
    private final List<String> permissions = new ArrayList<>();
    private Color color;
    private String prefix;
    private int weight;
    private boolean defaultRank;

    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.color = Color.WHITE;
        this.weight = 0;
        this.defaultRank = false;

        ranks.put(uuid, this);
    }

    public Rank(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        ranks.put(uuid, this);
    }

    public Rank(UUID uuid, String name, Color color, int weight, boolean defaultRank) {
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

                Rank rank = new Rank(UUID.fromString(document.getString("_id")), document.getString("name"));
                rank.load(document);

                rank.inherited.clear();
                List<UUID> ranksToInherit = document.getList("inherits", String.class).
                        stream()
                        .map(UUID::fromString).collect(Collectors.toList());

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

    public static Rank getById(UUID uuid) {
//        Rank cachedRank = ranks.get(uuid);
//
//        if(cachedRank != null) { // TODO: Maybe its own method?
//            return cachedRank;
//        }
        Document document = collection.find(Filters.eq("_id", uuid.toString())).first();

        if (document != null) {
            Rank rank = new Rank(UUID.fromString(document.getString("_id")),
                    document.getString("name"),
                    new Color(document.getInteger("color")),
                    document.getInteger("weight"),
                    document.getBoolean("defaultRank"));

            rank.setPrefix(document.getString("prefix"));

            rank.getPermissions().addAll(document.getList("permissions", String.class));

            rank.getInherited().addAll(document.getList("inherits", String.class).
                    stream()
                    .map(s -> Rank.getById(UUID.fromString(s)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            return rank;
        }

        return null;
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

    public ChatColor getChatColor() {
        return ChatColor.of(this.color);
    }

    public boolean isDefaultRank() {
        return this.defaultRank;
    }

    public boolean hasPermission(String permission) {
        return hasPermission(permission, true);
    }

    public boolean hasPermission(String permission, boolean includeInheritance) {
        if (this.permissions.contains(permission)) {
            return true;
        }

        if (includeInheritance) {
            for (Rank rank : this.inherited) {
                if (rank.hasPermission(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canInherit(Rank rankToCheck) {
        if (this == rankToCheck || this.inherited.contains(rankToCheck) || rankToCheck.inherited.contains(this)) {
            return false;
        }

        for (Rank rank : this.inherited) {
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
            for (Rank rank : this.inherited) {
                permissions.addAll(rank.getAllPermissions());
            }
        }

        return permissions;
    }

    public void refreshPlayers() {
        for(Profile profile : Profile.getProfiles().values()) {
            for(Grant grant : profile.getCachedGrants()) {
                if (grant.getRank().equals(this)) {
                    profile.reloadGrants();
                    return;
                }
            }
        }
    }

    private void load(Document document) {
        if (document == null) {
            return;
        }

        this.color = new Color(document.getInteger("color"));
        this.prefix = document.getString("prefix");
        this.weight = document.getInteger("weight");
        this.defaultRank = document.getBoolean("defaultRank");

        this.permissions.clear();
        this.permissions.addAll(document.getList("permissions", String.class));

        this.inherited.clear();
        this.inherited.addAll(document.getList("inherits", String.class).
                stream()
                .map(s -> Rank.getById(UUID.fromString(s)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public void save() {
        Document document = new Document();
        document.put("_id", this.uuid.toString());
        document.put("name", this.name);
        document.put("color", this.color.getRGB());
        document.put("prefix", this.prefix);
        document.put("weight", this.weight);
        document.put("defaultRank", this.defaultRank);
        document.put("permissions", this.permissions);
        document.put("inherits", this.inherited.stream().map(Rank::getUuid).map(UUID::toString).collect(Collectors.toList()));

        collection.replaceOne(Filters.eq("_id", this.uuid.toString()), document,
                new ReplaceOptions().upsert(true));
    }

    public void delete() {
        ranks.remove(uuid);
        collection.deleteOne(Filters.eq("_id", this.uuid.toString()));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Rank && ((Rank) object).uuid.equals(this.uuid);
    }

}