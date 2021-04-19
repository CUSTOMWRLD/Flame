package com.customwrld.flame.util;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.flame.Flame;
import com.customwrld.flame.Config;
import com.customwrld.flame.chat.ChatFilter;
import com.customwrld.flame.chat.ChatListener;
import com.customwrld.flame.commands.bungeecord.AlertCommand;
import com.customwrld.flame.commands.bungeecord.FindCommand;
import com.customwrld.flame.commands.bungeecord.GListCommand;
import com.customwrld.flame.commands.bungeecord.SendCommand;
import com.customwrld.flame.commands.chat.AdminChatCommand;
import com.customwrld.flame.commands.chat.ClearChatCommand;
import com.customwrld.flame.commands.chat.MuteChatCommand;
import com.customwrld.flame.commands.chat.StaffChatCommand;
import com.customwrld.flame.commands.conversations.ForceMessageCommand;
import com.customwrld.flame.commands.conversations.IgnoreCommand;
import com.customwrld.flame.commands.conversations.MessageCommand;
import com.customwrld.flame.commands.conversations.ReplyCommand;
import com.customwrld.flame.commands.essentials.*;
import com.customwrld.flame.commands.grant.ClearGrantsCommand;
import com.customwrld.flame.commands.grant.GrantCommand;
import com.customwrld.flame.commands.grant.GrantsCommand;
import com.customwrld.flame.commands.grant.SetRankCommand;
import com.customwrld.flame.commands.options.*;
import com.customwrld.flame.commands.staff.PermsCommand;
import com.customwrld.flame.commands.player.*;
import com.customwrld.flame.commands.punishments.*;
import com.customwrld.flame.commands.staff.RankCommand;
import com.customwrld.flame.commands.staff.*;
import com.customwrld.flame.commands.staff.listener.FreezeListener;
import com.customwrld.flame.menus.MenuListener;
import com.customwrld.flame.network.Server;
import com.customwrld.flame.network.ServerType;
import com.customwrld.flame.pigeon.payloads.UpdateServerInfoPayload;
import com.customwrld.flame.profile.AuthListener;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.ProfileListener;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.store.tebex.TebexAPI;
import com.customwrld.flame.sync.AccountSync;
import com.customwrld.flame.sync.discord.DiscordListener;
import com.customwrld.flame.sync.discord.DiscordSync;
import dev.jorel.commandapi.CommandAPICommand;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.util.*;

public class Initialize {

    public static List<BukkitTask> taskList = new ArrayList<>();

    public static void init(Flame flame) {
        CustomLib customLib = CustomLib.getInstance();
        DisabledCommands.Initialize();

        Arrays.asList(
                FixAllCommand.command(),
                BroadcastCommand.command(),
                FreezeServerCommand.command(),
                UnFreezeServerCommand.command(),
                FreezeCommand.command(),
                UnFreezeCommand.command(),
                ClearCommand.command(),
                KillCommand.command(),
                PingCommand.command(),
                ServerStatsCommand.command(),
                CraftCommand.command(),
                FlyCommand.command(),
                GameModeCommand.command(),
                FeedCommand.command(),
                LockDownCommand.command(),
                KickAllCommand.command(),
                FixCommand.command(),
                HealCommand.command(),
                PotionCommand.command(),
                StackCommand.command(),
                RenameCommand.command(),
                EnchantCommand.command(),
                DesyncCommand.command(),
                SyncCommand.command(),
                GListCommand.command(),
                ForceMessageCommand.command(),
                ToggleStaffMessagesCommand.command(),
                PermsCommand.command(),
                ForceStopCommand.command(),
                FindCommand.command(),
                AlertCommand.command(),
                SendCommand.command(),
                OpCommand.command(),
                IgnoreCommand.command(),
                ListCommand.command(),
                HubCommand.command(),
                MuteChatCommand.command(),
                AdminChatCommand.command(),
                TwoFactorAuthCommand.command(),
                OptionsCommand.command(),
                ToggleGlobalChatCommand.command(),
                TogglePrivateMessagesCommand.command(),
                ToggleSoundsCommand.command(),
                MessageCommand.command(),
                ReplyCommand.command(),
                RankCommand.command(),
                NetworkCommand.command(),
                ClearChatCommand.command(),
                StaffChatCommand.command(),
                StopCommand.command(),
                ClearGrantsCommand.command(),
                GrantCommand.command(),
                GrantsCommand.command(),
                SetRankCommand.command(),
                BanCommand.command(),
                BlacklistCommand.command(),
                ClearPunishmentsCommand.command(),
                HistoryCommand.command(),
                KickCommand.command(),
                MuteCommand.command(),
                UnbanCommand.command(),
                UnblacklistCommand.command(),
                UnmuteCommand.command(),
                WarnCommand.command(),
                StoreCommand.command()
        ).forEach(CommandAPICommand::register);

        flame.getElevatedStaff().addAll(Arrays.asList(
                UUID.fromString("239c8573-0675-4a78-82f3-d178c72d5dc9"), // oHate
                UUID.fromString("91121d88-8a07-4bef-bc78-c80484e1b6b6"), // BitCrackIGN
                UUID.fromString("a210da7b-e671-42f3-bf5b-eeec0282b5cb"), // Custom
                UUID.fromString("ccff0c05-64eb-4383-8cd3-ef3215652558")  // FreakingChicken
        ));

        Rank.init();

        PluginManager pluginManager = Bukkit.getPluginManager();

        Arrays.asList(
                new FreezeListener(),
                new AuthListener(),
                new MenuListener(),
                new ProfileListener(),
                new ChatListener(),
                new DisabledCommands()
        ).forEach(listener -> pluginManager.registerEvents(listener, flame));

        ConfigurationFile config = Flame.get().getMainConfig();

        flame.setPlaceholders(new Placeholders("CUSTOMWRLD", "flame", flame.getDescription().getVersion()));
        flame.getPlaceholders().register();

        AccountSync.init();

        ChatFilter.init();

        DiscordSync discordSync = AccountSync.getDiscordSync();

        discordSync.guild = customLib.getJda().getGuildById(config.getString("DISCORD.GUILDID"));

        if (discordSync.guild != null) {
            discordSync.channel = discordSync.guild.getTextChannelById(config.getString("DISCORD.SYNC.CHANNELID"));
        }

        customLib.getJda().addEventListener(new DiscordListener());

        TebexAPI.setSecret(config.getString("TEBEX.SECRET"));

        Flame.get().getStoreManager().initialize();

        taskList.addAll(Arrays.asList(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Profile profile : Profile.getProfiles().values()) {
                            for (Grant grant : profile.getGrants()) {
                                if (grant.hasExpired() && !grant.isRemoved()) {
                                    grant.setRemovedBy(null);
                                    grant.setRemovedAt(System.currentTimeMillis());
                                    grant.setRemovedReason("Grant Expired");
                                    grant.setRemoved(true);
                                    Grant.expireGrant(profile.getPlayer(), grant);
                                }
                            }
                        }
                    }
                }.runTaskTimer(Flame.get(), 0, 20),
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Flame.get().isShuttingDown()) {
                            this.cancel();
                            return;
                        }
                        new UpdateServerInfoPayload(
                                ServerType.valueOf(Config.SERVER_TYPE.format()),
                                customLib.getServerName(),
                                StringUtils.join(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new), ", "),
                                ManagementFactory.getRuntimeMXBean().getUptime(),
                                MinecraftServer.getServer().recentTps[0],
                                Bukkit.getOnlinePlayers().size(),
                                Bukkit.getMaxPlayers()
                        ).send();
                        for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
                            String serverName = ent.getKey();
                            Server server = ent.getValue();

                            if((System.currentTimeMillis() - server.getLastUpdate()) >= (1000 * 30)) {
                                Server.getServers().remove(serverName);
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(flame, 0, 20 * 10)));
    }
}