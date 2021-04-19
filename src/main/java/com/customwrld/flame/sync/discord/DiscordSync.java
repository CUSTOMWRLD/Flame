package com.customwrld.flame.sync.discord;

import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.rank.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;

public class DiscordSync {
    public Guild guild;
    public TextChannel channel;

    public Map<Profile, SyncCode> syncCodes = Collections.synchronizedMap(new HashMap<>());

    public void startSync(Profile profile) {
        if (profile.isSyncedWithDiscord()) {
            Arrays.asList(
                    CC.CHAT_BAR,
                    CC.RED + "You're already synchronized!",
                    CC.RED + "If you want to change it, desynchronize with '/desync discord' and synchronize again!",
                    CC.CHAT_BAR)
                    .forEach(profile.getPlayer()::sendMessage);
            return;
        }

        SyncCode syncCode = new SyncCode(RandomStringUtils.random(6, true, true), System.currentTimeMillis());
        syncCodes.put(profile, syncCode);

        Arrays.asList(
                CC.CHAT_BAR,
                CC.GREEN + "Your synchronization code is:",
                CC.GREEN + syncCode.getCode(),
                "",
                CC.GREEN + "You have 5 minutes to enter it on our Discord in the sync channel!",
                CC.CHAT_BAR).forEach(profile.getPlayer()::sendMessage);
    }

    public void submitSyncCode(Profile profile, Member member) {
        Player player = profile.getPlayer();
        String discordId = member.getId();

        channel.sendMessage(new EmbedBuilder().setColor(new Color(255, 140, 0)).addField("**Synchronized Successfully**", "Successfully synced your discord account to " + profile.getName() + "!", false).build()).queue();

        Arrays.asList(
                CC.CHAT_BAR,
                CC.GREEN + "Synchronized successfully!",
                CC.GREEN + "Enjoy your ranks on our Discord!",
                "",
                CC.GREEN + "Someone else used your code? No worries, just use '/desync discord'!",
                CC.CHAT_BAR)
                .forEach(player::sendMessage);

        profile.setDiscord(discordId);

        ConfigurationFile config = Flame.get().getMainConfig();
        ConfigurationSection syncSection = config.getConfiguration().getConfigurationSection("DISCORD.SYNC.RANKS");
        if (syncSection != null) {
            for (Grant grant : profile.getGrants()) {
                for (String key : syncSection.getKeys(false)) {
                    if (grant.getRank().getUuid().toString().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".RANKID"))) {
                        guild.addRoleToMember(member, member.getGuild().getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                    }
                }
            }
        }

        addBoosterRank(profile, member);

        profile.save();
        profile.recalculatePlayer(profile.getPlayer());
    }

    public void desync(Profile profile) {
        if (!profile.isSyncedWithDiscord()) {
            Arrays.asList(
                    CC.CHAT_BAR,
                    CC.RED + "You're not synchronized!",
                    CC.RED + "You can synchronize with '/sync discord'!",
                    CC.CHAT_BAR).forEach(profile.getPlayer()::sendMessage);
            return;
        }

        Arrays.asList(
                CC.CHAT_BAR,
                "&cYour discord account has been desynced.",
                "&CYou can synchronize again with '/sync discord'.",
                CC.CHAT_BAR)
                .forEach(message -> profile.getPlayer().sendMessage(CC.translate(message)));

        removeAllSyncRoles(profile);
        removeBoosterRank(profile);

        profile.setDiscord(null);

        profile.save();
        profile.recalculatePlayer(profile.getPlayer());
    }

    public void resync(Profile profile) {
        if (!profile.isSyncedWithDiscord()) {
            return;
        }

        String discord = profile.getDiscord();

        Member member = guild.getMemberById(discord);
        if (member != null) {
            ConfigurationFile config = Flame.get().getMainConfig();
            ConfigurationSection syncSection = config.getConfiguration().getConfigurationSection("DISCORD.SYNC.RANKS");
            if (syncSection != null) {
                for (Role role : member.getRoles()) {
                    for (String key : syncSection.getKeys(false)) {
                        if (role.getId().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))) {
                            if (profile.getGrants().stream().noneMatch(grant -> !grant.hasExpired() && grant.getRank().getUuid().toString().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".RANKID")))) {
                                guild.removeRoleFromMember(member, member.getGuild().getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                            }
                        }
                    }
                }
                for (Grant grant : profile.getGrants()) {
                    for (String key : syncSection.getKeys(false)) {
                        if (!grant.hasExpired() && grant.getRank().getUuid().toString().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".RANKID"))) {
                            guild.addRoleToMember(member, guild.getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                        }
                    }
                }
            }

            if (member.getTimeBoosted() == null) {
                removeBoosterRank(profile);
            } else {
                addBoosterRank(profile, member);
            }

            profile.save();
            profile.recalculatePlayer(profile.getPlayer());
        }
    }

    public boolean removeAllSyncRoles(Profile profile) {
        if (!profile.isSyncedWithDiscord()) {
            return false;
        }

        String discord = profile.getDiscord();

        ConfigurationFile config = Flame.get().getMainConfig();
        ConfigurationSection syncSection = config.getConfiguration().getConfigurationSection("DISCORD.SYNC.RANKS");
        if (syncSection != null) {
            Member member = guild.getMemberById(discord);
            if (member != null) {
                for (String key : syncSection.getKeys(false)) {
                    guild.removeRoleFromMember(member, guild.getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeSyncRole(Profile profile, Rank rank) {
        if (!profile.isSyncedWithDiscord()) {
            return false;
        }

        String discord = profile.getDiscord();

        ConfigurationFile config = Flame.get().getMainConfig();
        ConfigurationSection syncSection = config.getConfiguration().getConfigurationSection("DISCORD.SYNC.RANKS");
        if (syncSection != null) {
            for (String key : syncSection.getKeys(false)) {
                if (rank.getUuid().toString().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".RANKID"))) {
                    Member member = guild.getMemberById(discord);
                    if (member != null) {
                        guild.removeRoleFromMember(member, guild.getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean addSyncRole(Profile profile, Rank rank) {
        if (!profile.isSyncedWithDiscord()) {
            return false;
        }

        String discord = profile.getDiscord();

        ConfigurationFile config = Flame.get().getMainConfig();
        ConfigurationSection syncSection = config.getConfiguration().getConfigurationSection("DISCORD.SYNC.RANKS");
        if (syncSection != null) {
            for (String key : syncSection.getKeys(false)) {
                if (rank.getUuid().toString().equals(config.getString("DISCORD.SYNC.RANKS." + key + ".RANKID"))) {
                    Member member = guild.getMemberById(discord);
                    if (member != null) {
                        guild.addRoleToMember(member, member.getGuild().getRoleById(config.getString("DISCORD.SYNC.RANKS." + key + ".ROLEID"))).queue();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void addBoosterRank(Profile profile, Member member) {
        ConfigurationFile config = Flame.get().getMainConfig();
        if (member.getTimeBoosted() != null && profile.getGrants().stream().noneMatch(grant -> grant.getRank().getUuid().toString().equals(config.getString("DISCORD.SYNC.BOOSTER.RANKID")))) {
            Rank rank = Rank.getRankByUuid(UUID.fromString(config.getString("DISCORD.SYNC.BOOSTER.RANKID")));
            Grant grant = new Grant(UUID.randomUUID(), rank, null, System.currentTimeMillis(), "Boosted our Discord server", Integer.MAX_VALUE);

            profile.getGrants().add(grant);
        }
    }

    private void removeBoosterRank(Profile profile) {
        profile.getGrants().removeIf(grant -> grant.getRank().getUuid().toString().equals(
                Flame.get().getMainConfig().getString("DISCORD.SYNC.BOOSTER.RANKID")
        ));
    }

    public void beginTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Profile, SyncCode> ent : syncCodes.entrySet()) {
                    if (ent.getValue().getGeneratedAt() + (60000 * 5) > System.currentTimeMillis()) {
                        syncCodes.remove(ent.getKey());
                    }
                }
            }
        }.runTaskTimerAsynchronously(Flame.get(), 0, 20 * 60);
    }
}
