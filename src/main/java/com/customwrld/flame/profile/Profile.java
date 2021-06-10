package com.customwrld.flame.profile;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CollectionsUtil;
import com.customwrld.customlib.util.UUIDUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.rank.Rank;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Getter
@Setter
public class Profile {

    @Getter
    private static final MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("profiles");
    @Getter
    private static final Map<UUID, Profile> profiles = new HashMap<>();

    private final UUID uuid;
    private final List<String> permissions;
    private final List<String> punishments;
    private final List<Punishment> cachedPunishments;
    private final List<Grant> grants;
    private final List<UUID> ignored;
    private final List<UUID> friends;
    private final OptionsProfile options;
    private final StaffProfile staffProfile;
    private Grant activeGrant;
    private String name;
    private String currentAddress;
    private String discordId;
    private Integer gold;
    private Long firstSeen;
    private Long lastSeen;
    private Long experience;
    private Map<String, Integer> gameModeGold;
    private boolean loaded;

    public Profile(String username, UUID uuid) {
        this.uuid = uuid;
        this.name = username;
        this.permissions = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.cachedPunishments = new ArrayList<>();
        this.grants = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.gameModeGold = new HashMap<>();
        this.options = new OptionsProfile();
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

//    public static List<Profile> getByDiscord(String discord) {
//        List<Profile> profiles = new ArrayList<>();
//        Bson filter = Filters.eq("discordId", discord);
//
//        try (MongoCursor<Document> cursor = collection.find(filter).iterator()) {
//            while (cursor.hasNext()) {
//                Document document = cursor.next();
//                profiles.add(new Profile(document.getString("name"),
//                        UUID.fromString(document.getString("uuid"))));
//            }
//        }
//
//        return profiles;
//    }
//
//    public boolean isSyncedWithDiscord() {
//        return this.discordId != null && !this.discordId.isEmpty();
//    }

    public boolean isIgnoring(Profile profile) {
        return ignored.contains(profile.getUuid());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    private void checkPunishments() {
        for (Punishment punishment : this.cachedPunishments) {
            if (!punishment.isRemoved() && punishment.hasExpired()) {
                punishment.setRemovedAt(System.currentTimeMillis());
                punishment.setRemovedReason("Punishment Expired");
                punishment.setRemoved(true);
                punishment.setRemovedBy(null);
                punishment.broadcast(this);
                punishment.save();
            }
        }
    }

    public Punishment getActivePunishmentByType(Punishment.Type type) {
        for (Punishment punishment : this.cachedPunishments) {
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired()) {
                return punishment;
            }
        }

        return null;
    }

    public int getPunishmentCountByType(Punishment.Type type) {
        int i = 0;

        for (Punishment punishment : this.cachedPunishments) {

            if (punishment.getType() == type) i++;
        }

        return i;
    }

    public Rank getActiveRank() {
        return this.activeGrant.getRank();
    }

    private void checkGrants(boolean save) {
        Player player = getPlayer();

        Iterator<Grant> iterator = this.grants.iterator();
        List<Grant> activeGrants = new ArrayList<>();

        while (iterator.hasNext()) {
            Grant grant = iterator.next();

            if (grant.getRank() == null || Rank.getRanks().get(grant.getRank().getUuid()) == null) {
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
                    Grant.expireGrant(this, grant);
                }
            }
        }

        if (activeGrants.isEmpty()) {
            Grant defaultGrant = new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null, System.currentTimeMillis(), "Default", Integer.MAX_VALUE);

            this.grants.add(defaultGrant);
            this.activeGrant = defaultGrant;

            if (player != null) {
                Grant.loadNewGrant(this, defaultGrant);
            }
        } else {
            this.activeGrant = activeGrants.get(0);
        }

        if(save) {
            this.save();
        }
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

    private void checkPlayer() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        player.setOp(this.staffProfile.isOperator());

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
        Document document = collection.find(Filters.eq("_id", this.uuid.toString())).first();

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
            this.staffProfile.setTwoFactorKey(staffDocument.getString("twoFactorKey"));
            this.staffProfile.setOperator(staffDocument.getBoolean("operator"));
            this.staffProfile.setReceivingStaffMessages(staffDocument.getBoolean("receivingStaffMessages"));
            this.staffProfile.setTwoFactor(staffDocument.getBoolean("twoFactor"));
            this.staffProfile.setLocked(staffDocument.getBoolean("locked"));

            this.discordId = document.getString("discordId");

            this.gold = document.getInteger("gold");

            this.gameModeGold.clear();
            this.gameModeGold = CollectionsUtil.genericMapToType(document.get("gameModeGold", Map.class), String.class, Integer.class);

            this.experience = document.getLong("experience");

            this.grants.clear();
            for (Map<?, ?> obj : document.getList("grants", Map.class, new ArrayList<>())) {
                this.grants.add(Grant.fromSimple(
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

            this.punishments.clear();
            this.punishments.addAll(document.getList("punishments", String.class));

            this.cachedPunishments.clear();
            this.cachedPunishments.addAll(this.punishments.stream().map(Punishment::getById).filter(Objects::nonNull).collect(Collectors.toList()));

            this.permissions.clear();
            this.permissions.addAll(document.getList("permissions", String.class));

            checkPunishments();
            checkGrants(false);
            checkPlayer();

            this.loaded = true;
        }

        this.save();
    }

    public void save() {
        Document document = new Document();
        document.put("_id", this.uuid.toString());
        document.put("name", this.name);
        document.put("firstSeen", this.firstSeen != null ? this.firstSeen.toString() : String.valueOf(System.currentTimeMillis()));
        document.put("lastSeen", this.lastSeen != null ? this.lastSeen.toString() : String.valueOf(System.currentTimeMillis()));
        document.put("currentAddress", this.currentAddress);

        Document optionsDocument = new Document();
        optionsDocument.put("receivingFriendRequests", this.options.isReceivingFriendRequests());
        optionsDocument.put("receivingPublicChat", this.options.isReceivingPublicChat());
        optionsDocument.put("receivingConversations", this.options.isReceivingConversations());
        optionsDocument.put("receivingMessageSounds", this.options.isReceivingMessageSounds());
        document.put("options", optionsDocument);

        Document staffDocument = new Document();
        staffDocument.put("twoFactorKey", this.staffProfile.getTwoFactorKey());
        staffDocument.put("operator", this.staffProfile.isOperator());
        staffDocument.put("receivingStaffMessages", this.staffProfile.isReceivingStaffMessages());
        staffDocument.put("twoFactor", this.staffProfile.isTwoFactor());
        staffDocument.put("locked", this.staffProfile.isLocked());
        document.put("staff", staffDocument);

        document.put("discord", this.discordId);

        document.put("gold", this.gold);
        document.put("gameModeGold", this.gameModeGold);
        document.put("experience", this.experience);

        List<HashMap<String, String>> simpleGrants = new ArrayList<>();

        for (Grant grant : this.grants) {
            simpleGrants.add(grant.simplify());
        }

        document.put("grants", simpleGrants);

        document.put("punishments", this.punishments);

        document.put("permissions", this.permissions);

        document.put("ignored", this.ignored);

        document.put("friends", this.friends);

        collection.replaceOne(Filters.eq("_id", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

}