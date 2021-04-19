package com.customwrld.flame.profile.punishment;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Punishment {

    public static PunishmentJsonSerializer SERIALIZER = new PunishmentJsonSerializer();
    public static PunishmentJsonDeserializer DESERIALIZER = new PunishmentJsonDeserializer();

    @Getter private final UUID uuid;
    @Getter private final PunishmentType type;
    @Getter private final UUID addedBy;
    @Getter private final long addedAt;
    @Getter private final String addedReason;
    @Getter private final long duration;
    @Getter @Setter private UUID removedBy;
    @Getter @Setter private long removedAt;
    @Getter @Setter private String removedReason;
    @Getter @Setter private boolean removed;

    public Punishment(UUID uuid, PunishmentType type, UUID addedBy, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.type = type;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
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

    public String getKickMessage() {
        String punishmentBar = "&9&m        &b&m        &f&m        &r &8[&3&l" + this.type.getReadable().toUpperCase() + "&8] &f&m        &b&m        &9&m        ";

        return CC.translate(punishmentBar +
                "\n\n&9Your account has been " + this.getType().getContext() + " from the CUSTOMWRLD Network.\n" +
                "\n&fReason: &9" + this.addedReason +
                "\n&fAdded On: &9" + TimeUtil.dateToString(new Date(this.getAddedAt())) + " &fat &9" + millisToTime(this.getAddedAt()) +
                (!this.isPermanent() ? "\n&fTime Remaining: &9" + this.getTimeRemaining() : "") +
                "\n\n" + punishmentBar);
    }

    public static String millisToTime(long millis) {  // TODO Merge with Custom Lib
        Date date = new Date(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("EST"));
        calendar.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(date);
    }

    public void sendEmbed(Profile profile) {
        JDA jda = CustomLib.getInstance().getJda();
        String name = profile.getName().replace("_", "\\_");

        Guild guild = jda.getGuildById(Config.DISCORD_GUILD.format());
        Color color = this.getType().getTypeData().getColor().getColor();

        if(guild != null) {
            TextChannel channel = guild.getTextChannelById(Config.DISCORD_PUNISHMENTS.format());

            if(channel != null) {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(name + " has been " + this.getContext())
                        .setColor(color)
                        .addField("**Player Name**", name, true)
                        .addField("**Player UUID**", profile.getUuid().toString(), true);

                if(profile.getDiscord() != null) {
                    User user = jda.getUserById(profile.getDiscord());

                    if (profile.isSyncedWithDiscord() && user != null) {
                        builder.addField("**Discord ID**", profile.getDiscord(), true)
                                .addField("**Discord Name**", user.getName() + "#" + user.getDiscriminator(), true);
                    }
                }

                if(!this.isRemoved()) {
                    if(this.getAddedBy() != null) {
                        builder.addField("**Staff Member Name**", Profile.getByUuid(this.getAddedBy()).getName(), true)
                                .addField("**Staff Member UUID**", this.getAddedBy().toString(), true);
                    } else {
                        builder.addField("**Staff Member Name**", "Console", true);
                    }
                    if(!this.isPermanent() && !this.hasExpired()) {
                        builder.addField("**Duration**", TimeUtil.millisToRoundedTime(duration), true);
                    }
                } else {
                    if(this.getRemovedBy() != null) {
                        builder.addField("**Staff Member Name**", Profile.getByUuid(this.getRemovedBy()).getName(), true)
                                .addField("**Staff Member UUID**", this.getRemovedBy().toString(), true);
                    } else {
                        builder.addField("**Staff Member Name**", "Console", true);
                    }
                }

                builder.addField("**Reason**", this.getAddedReason(), true)
                        .setThumbnail("https://crafatar.com/avatars/" + profile.getUuid().toString().replaceAll("-", ""));

                channel.sendMessage(builder.build()).queue();
            }
        }
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