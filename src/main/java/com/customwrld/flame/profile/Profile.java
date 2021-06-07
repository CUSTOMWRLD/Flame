package com.customwrld.flame.profile;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CollectionsUtil;
import com.customwrld.customlib.util.UUIDUtil;
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
import java.util.concurrent.ExecutionException;

@Getter @Setter
public class Profile {

    public static final MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("profiles");
    public static final Map<UUID, Profile> profiles = new HashMap<>();

    private final UUID uuid;

    private final List<String> ipAddresses;
    private final List<String> permissions;

    private final List<Punishment> punishments;
    private final List<Grant> grants;

    private final List<UUID> knownAlts;
    private final List<UUID> ignored;
    private final List<UUID> friends;

    private final OptionsProfile options;
    private final StaffProfile staffProfile;
    private final ProfileConversations conversations;

    private Grant activeGrant;

    private String name;
    private String currentAddress;
    private String discordId;

    private Long firstSeen;
    private Long lastSeen;

    private boolean loaded;

    private int gold;
    private Map<String, Integer> gamemodeGold;
    private long experience;

    public Profile(String username, UUID uuid) {
        this.uuid = uuid;
        this.name = username;
        this.grants = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.ipAddresses = new ArrayList<>();
        this.knownAlts = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.gamemodeGold = new HashMap<>();
        this.options = new OptionsProfile();
        this.conversations = new ProfileConversations(this);
        this.staffProfile = new StaffProfile();

        reload();
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

        UUIDUtil.UUIDPair uuidPair;

        try {
            uuidPair = UUIDUtil.getUUID(username).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return new Profile(uuidPair.getName(), uuidPair.getUuid());
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
        Bson filter = Filters.eq("discordId", discord);

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
        return this.discordId != null && !this.discordId.isEmpty();
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

    private void checkPunishments() {
        for (Punishment punishment : punishments) {
            if (!punishment.isRemoved() && punishment.hasExpired()) {
                punishment.setRemovedAt(System.currentTimeMillis());
                punishment.setRemovedReason("Punishment Expired");
                punishment.setRemoved(true);
                punishment.setRemovedBy(null);
                punishment.sendEmbed(this.uuid);
                punishment.broadcast(true, this);
            }
        }
    }

    public Rank getActiveRank() {
        return activeGrant.getRank();
    }

    private void checkGrants() {
        Player player = getPlayer();

        Iterator<Grant> iterator = this.grants.iterator();
        List<Grant> activeGrants = new ArrayList<>();

        while (iterator.hasNext()) {
            Grant grant = iterator.next();

            if (grant.getRank() == null || Rank.ranks.get(grant.getRank().getUuid()) == null) {
                iterator.remove();
            } else if (!grant.isRemoved() && !grant.hasExpired()) {
                activeGrants.add(grant);
            }

        }

        activeGrants.sort(Grant.COMPARATOR);

        for (Grant grant : this.grants) {
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

            this.grants.add(defaultGrant);
            this.activeGrant = defaultGrant;

            if (player != null) {
                Grant.loadNewGrant(this, player, defaultGrant);
            }
        } else {
            this.activeGrant = activeGrants.get(0);
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

    private void checkPlayer() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        player.setOp(this.staffProfile.operator());

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getAttachment() == null || !attachmentInfo.getAttachment().getPlugin().equals(Flame.getFlame())) {
                continue;
            }

            attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> attachmentInfo.getAttachment().unsetPermission(permission));
        }

        PermissionAttachment attachment = player.addAttachment(Flame.getFlame());

        for (String permission : this.getAllPermissions()) {
            attachment.setPermission(permission, true);
        }

        player.recalculatePermissions();
    }

    public void reload() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            if (this.name == null) {
                this.name = document.getString("name");
            }

            this.firstSeen = Long.parseLong(document.getString("firstSeen"));
            this.lastSeen = Long.parseLong(document.getString("lastSeen"));
            this.currentAddress = document.getString("currentAddress");

            Document optionsDocument = (Document) document.get("options");
            this.options.setReceivingFriendRequests(optionsDocument.getBoolean("receivingFriendRequests"));
            this.options.setReceivingPublicChat(optionsDocument.getBoolean("receivingPublicChat"));
            this.options.setReceivingConversations(optionsDocument.getBoolean("receivingConversations"));
            this.options.setReceivingMessageSounds(optionsDocument.getBoolean("receivingMessageSounds"));

            Document staffDocument = (Document) document.get("staff");
            this.staffProfile.authKey(staffDocument.getString("authKey"));
            this.staffProfile.operator(staffDocument.getBoolean("operator"));
            this.staffProfile.staffMessagesEnabled(staffDocument.getBoolean("staffMessagesEnabled"));
            this.staffProfile.authEnabled(staffDocument.getBoolean("authEnabled"));
            this.staffProfile.locked(staffDocument.getBoolean("locked"));

            this.discordId = document.getString("discordId");

            this.gold = document.getInteger("gold");

            this.gamemodeGold.clear();
            this.gamemodeGold = CollectionsUtil.genericMapToType(document.get("gamemodeGold", Map.class), String.class, Integer.class);

            this.experience = document.getLong("experience");

            this.ipAddresses.clear();
            this.ipAddresses.addAll(document.getList("ipAddresses", String.class, new ArrayList<>()));

            this.grants.clear();
            for (Map<?, ?> obj : document.getList("grants", Map.class, new ArrayList<>())) {
                this.grants.add(Grant.fromSimple(
                        CollectionsUtil.genericMapToType(obj, String.class, String.class)));
            }

            this.punishments.clear();
            for (Map<?, ?> obj : document.getList("punishments", Map.class, new ArrayList<>())) {
                this.punishments.add(Punishment.fromSimple(
                        CollectionsUtil.genericMapToType(obj, String.class, String.class)));
            }

            this.ignored.clear();
            for (String uuid : document.getList("ignored", String.class)) {
                UUID ignoredUUID = UUID.fromString(uuid);
                this.ignored.add(ignoredUUID);
            }

            this.friends.clear();
            for (String uuid : document.getList("friends", String.class)) {
                UUID friendUUID = UUID.fromString(uuid);
                this.friends.add(friendUUID);
            }

            this.permissions.clear();
            this.permissions.addAll(document.getList("permissions", String.class));

            checkPunishments();
            checkGrants();
            checkPlayer();

            this.loaded = true;
        } else {
            this.loaded = false;
        }
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
        optionsDocument.put("receivingFriendRequests", options.isReceivingFriendRequests());
        optionsDocument.put("receivingPublicChat", options.isReceivingPublicChat());
        optionsDocument.put("receivingConversations", options.isReceivingConversations());
        optionsDocument.put("receivingMessageSounds", options.isReceivingMessageSounds());
        document.put("options", optionsDocument);

        Document staffDocument = new Document();
        staffDocument.put("authKey", staffProfile.authKey());
        staffDocument.put("operator", staffProfile.operator());
        staffDocument.put("staffMessagesEnabled", staffProfile.staffMessagesEnabled());
        staffDocument.put("authEnabled", staffProfile.authEnabled());
        staffDocument.put("locked", staffProfile.locked());
        document.put("staff", staffDocument);

        document.put("discord", discordId);

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

        document.put("ignored", ignored);

        document.put("friends", friends);

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

}