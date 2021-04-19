package com.customwrld.flame.profile;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CollectionsUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.conversation.ProfileConversations;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.rank.Rank;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class Profile {

    @Getter private static Map<UUID, Profile> profiles = new HashMap<>();
    @Getter private static MongoCollection<Document> collection = CustomLib.getInstance().getMongoDatabase().getCollection("profiles");

    @Getter private final UUID uuid;
    @Getter private final List<UUID> knownAlts;

    @Getter @Setter private String name;
    @Getter @Setter private String currentAddress;
    @Getter @Setter private String discord;
    @Getter @Setter private String currentServer;

    @Getter @Setter private Long firstSeen;
    @Getter @Setter private Long lastSeen;

    @Getter private final List<String> ipAddresses;
    @Getter private final List<String> permissions;
    @Getter private final List<Punishment> punishments;
    @Getter private final List<Grant> grants;
    @Getter private final List<Profile> ignored;

    @Getter private final OptionsProfile options;
    @Getter private final StaffProfile staffProfile;
    @Getter private final ProfileConversations conversations;

    @Getter @Setter private boolean online;
    @Getter @Setter private boolean loaded;

    @Getter private Grant activeGrant;

    @Getter @Setter private int gold;
    @Getter @Setter private Map<String, Integer> gamemodeGold;
    @Getter @Setter private long experience;

    public Profile(String username, UUID uuid) {
        this.uuid = uuid;
        this.name = username;
        this.grants = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.ipAddresses = new ArrayList<>();
        this.knownAlts = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.gamemodeGold = new HashMap<>();
        this.options = new OptionsProfile();
        this.conversations = new ProfileConversations(this);
        this.staffProfile = new StaffProfile();
        load();
    }

    public static Profile getByUuid(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }

        return new Profile(null, uuid);
    }

    public static Profile getByUsername(String username) {
        Player player = Bukkit.getPlayer(username);

        if (player != null) {
            return profiles.get(player.getUniqueId());
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (offlinePlayer.hasPlayedBefore()) {
            if (profiles.containsKey(offlinePlayer.getUniqueId())) {
                return profiles.get(offlinePlayer.getUniqueId());
            }

            return new Profile(offlinePlayer.getName(), offlinePlayer.getUniqueId());
        }

        return null;
    }

    public static List<Profile> getByIpAddress(String ipAddress) {
        List<Profile> profiles = new ArrayList<>();
        Bson filter = Filters.eq("currentAddress", ipAddress);

        try (MongoCursor<Document> cursor = collection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                profiles.add(new Profile(document.getString("name"),
                        UUID.fromString(document.getString("uuid"))));
            }
        }

        return profiles;
    }

    public static List<Profile> getByDiscord(String discord) {
        List<Profile> profiles = new ArrayList<>();
        Bson filter = Filters.eq("discord", discord);

        try (MongoCursor<Document> cursor = collection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                profiles.add(new Profile(document.getString("name"),
                        UUID.fromString(document.getString("uuid"))));
            }
        }

        return profiles;
    }

    public boolean isSyncedWithDiscord() {
        return discord != null && !discord.isEmpty();
    }

    public boolean isIgnoring(Profile profile) {
        return ignored.contains(profile);
    }

    public boolean isIgnoring(Player player) {
        return isIgnoring(Profile.getByUuid(player.getUniqueId()));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Punishment getActivePunishmentByType(PunishmentType type) {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired()) {
                return punishment;
            }
        }

        return null;
    }

    public void checkExpiredPunishments() {
        for (Punishment punishment : punishments) {
            if (!punishment.isRemoved() && punishment.hasExpired()) {
                punishment.setRemovedAt(System.currentTimeMillis());
                punishment.setRemovedReason("Punishment Expired");
                punishment.setRemoved(true);
                punishment.setRemovedBy(null);
                punishment.sendEmbed(this);
                punishment.broadcast(true, this);
            }
        }
    }

    public Rank getActiveRank() {
        return activeGrant.getRank();
    }

    public void checkGrants() {
        Player player = getPlayer();

        List<Grant> removableGrants = new ArrayList<>();
        List<Grant> activeGrants = new ArrayList<>();

        for (Grant grant : grants) {
            if (grant.getRank() == null || Rank.getRanks().get(grant.getRank().getUuid()) == null) {
                removableGrants.add(grant);
            } else if (!grant.isRemoved() && !grant.hasExpired()) {
                activeGrants.add(grant);
            }
        }

        grants.removeAll(removableGrants);

        activeGrants.sort((rank1, rank2) -> rank2.getRank().getWeight() - rank1.getRank().getWeight());

        for (Grant grant : grants) {
            if (!grant.isRemoved() && grant.hasExpired()) {
                grant.setRemovedAt(System.currentTimeMillis());
                grant.setRemovedReason("Grant Expired");
                grant.setRemoved(true);

                if (player != null) {
                    Grant.expireGrant(player, grant);
                }
            }
        }

        if (activeGrants.isEmpty()) {
            Grant defaultGrant = new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null, System.currentTimeMillis(), "Default", Integer.MAX_VALUE);

            grants.add(defaultGrant);
            activeGrant = defaultGrant;

            if (player != null) {
                Grant.loadNewGrant(this, player, defaultGrant);
            }
        } else {
            activeGrant = activeGrants.get(0);
        }

        this.save();
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.addAll(this.getRankPermissions());

        permissions.addAll(this.getPermissions());

        return permissions;
    }

    public List<String> getRankPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Grant grant : this.grants) {
            permissions.addAll(grant.getRank().getAllPermissions());
        }
        return permissions;
    }

    public int getPunishmentCountByType(PunishmentType type) {
        int i = 0;

        for (Punishment punishment : punishments) {
            if (punishment.getType() == type) i++;
        }

        return i;
    }

    public void recalculatePlayer(Player player) {
        if (player == null) {
            return;
        }

        Flame plugin = Flame.get();

        player.setOp(staffProfile.operator());

        checkGrants();

        if (!player.hasPermission("minecraft.command.gamemode") && player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.setGameMode(GameMode.SURVIVAL));
        }

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getAttachment() == null || !attachmentInfo.getAttachment().getPlugin().equals(Flame.get())) {
                continue;
            }

            attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> {
                attachmentInfo.getAttachment().unsetPermission(permission);
            });
        }

        PermissionAttachment attachment = player.addAttachment(Flame.get());

        for (String permission : this.getAllPermissions()) {
            attachment.setPermission(permission, true);
        }

        player.recalculatePermissions();
    }

    private void load() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            if (name == null) {
                name = document.getString("name");
            }

            firstSeen = Long.parseLong(document.getString("firstSeen"));
            lastSeen = Long.parseLong(document.getString("lastSeen"));
            currentAddress = document.getString("currentAddress");

            Document optionsDocument = (Document) document.get("options");
            options.publicChatEnabled(optionsDocument.getBoolean("publicChatEnabled"));
            options.conversationsEnabled(optionsDocument.getBoolean("conversationsEnabled"));
            options.soundsEnabled(optionsDocument.getBoolean("soundsEnabled"));

            Document staffDocument = (Document) document.get("staff");
            staffProfile.authKey(staffDocument.getString("authKey"));
            staffProfile.operator(staffDocument.getBoolean("operator"));
            staffProfile.staffMessagesEnabled(staffDocument.getBoolean("staffMessagesEnabled"));
            staffProfile.authEnabled(staffDocument.getBoolean("authEnabled"));
            staffProfile.locked(staffDocument.getBoolean("locked"));

            discord = document.getString("discord");

            online = document.getBoolean("isOnline");
            currentServer = document.getString("currentServer");

            gold = document.getInteger("gold");

            gamemodeGold = CollectionsUtil.genericMapToType(document.get("gamemodeGold", Map.class), String.class, Integer.class);

            experience = document.getLong("experience");

            ipAddresses.addAll(document.getList("ipAddresses", String.class, new ArrayList<>()));

            for (Map<?, ?> obj : document.getList("grants", Map.class, new ArrayList<>())) {
                this.grants.add(Grant.fromSimple(
                        CollectionsUtil.genericMapToType(obj, String.class, String.class)));
            }

            for (Map<?, ?> obj : document.getList("punishments", Map.class, new ArrayList<>())) {
                this.punishments.add(Punishment.fromSimple(
                        CollectionsUtil.genericMapToType(obj, String.class, String.class)));
            }

            for (String uuid : document.getList("ignored", String.class)) {
                UUID ignoredUUID = UUID.fromString(uuid);
                this.ignored.add(Profile.getByUuid(ignoredUUID));
            }

            permissions.clear();
            permissions.addAll(document.getList("permissions", String.class));

            checkExpiredPunishments();
        }

        loaded = true;
    }

    public void save() {
        Document document = new Document();
        document.put("name", name);
        document.put("uuid", uuid.toString());
        document.put("firstSeen", firstSeen != null ? firstSeen.toString() : String.valueOf(System.currentTimeMillis()));
        document.put("lastSeen", lastSeen != null ? lastSeen.toString() : String.valueOf(System.currentTimeMillis()));
        document.put("currentAddress", currentAddress);
        document.put("ipAddresses", ipAddresses);

        Document optionsDocument = new Document();
        optionsDocument.put("publicChatEnabled", options.publicChatEnabled());
        optionsDocument.put("conversationsEnabled", options.conversationsEnabled());
        optionsDocument.put("soundsEnabled", options.soundsEnabled());
        document.put("options", optionsDocument);

        Document staffDocument = new Document();
        staffDocument.put("authKey", staffProfile.authKey());
        staffDocument.put("operator", staffProfile.operator());
        staffDocument.put("staffMessagesEnabled", staffProfile.staffMessagesEnabled());
        staffDocument.put("authEnabled", staffProfile.authEnabled());
        staffDocument.put("locked", staffProfile.locked());
        document.put("staff", staffDocument);

        document.put("discord", discord);

        document.put("isOnline", online);
        document.put("currentServer", currentServer);

        document.put("gold", gold);
        document.put("gamemodeGold", gamemodeGold);
        document.put("experience", experience);

        List<HashMap<String, String>> simpleGrants = new ArrayList<>();

        for (Grant grant : grants) {
            simpleGrants.add(grant.simplify());
        }

        document.put("grants", simpleGrants);

        List<HashMap<String, String>> simplePunishments = new ArrayList<>();
        for (Punishment punishment : punishments) {
            simplePunishments.add(punishment.simplify());
        }
        document.put("punishments", simplePunishments);

        document.put("permissions", permissions);

        List<String> simpleIgnored = new ArrayList<>();

        for (Profile profile : ignored) {
            simpleIgnored.add(profile.getUuid().toString());
        }

        document.put("ignored", simpleIgnored);

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

}