package com.customwrld.flame;

import com.customwrld.commonlib.CommonLib;
import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.network.Gamemode;
import com.customwrld.commonlib.modules.pigeon.payloads.network.ServerUpdatePayload;
import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.assemble.Assemble;
import com.customwrld.customlib.assemble.AssembleStyle;
import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.customlib.util.ClassUtils;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.chat.ChatFilter;
import com.customwrld.flame.design.ScoreboardAdapter;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.listeners.ChatListener;
import com.customwrld.flame.listeners.CoreListener;
import com.customwrld.flame.listeners.ProfileListener;
import com.customwrld.flame.pigeon.Listeners;
import com.customwrld.flame.store.StoreManager;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.DisabledCommands;
import com.customwrld.pigeon.Pigeon;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class Flame extends JavaPlugin {

    // TODO: Replace NamedTextColor with MC.CC.XXXX
    // TODO: /mypunishments and /mygrants

    @Getter private static Flame flame;

    private Assemble assemble;

    private ConfigurationFile mainConfig;

    private GoogleAuthenticator auth;

    private StoreManager storeManager;

    private List<UUID> elevatedStaff;
    private List<Command> registeredCommands;

    private static boolean registerCommand(Flame flame, Class<?> commandClass) {
        try {
            if (!Command.class.isAssignableFrom(commandClass)) {
                return false;
            }
            Command command = (Command) commandClass.getDeclaredConstructor().newInstance();
            command.command().register();
            flame.getRegisteredCommands().add(command);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEnable() {
        flame = this;

        this.assemble = new Assemble(this, new ScoreboardAdapter());
        assemble.setTicks(20);
        assemble.setAssembleStyle(AssembleStyle.KOHI);

        this.mainConfig = new ConfigurationFile(this, "config");

        this.auth = new GoogleAuthenticator();

        this.storeManager = new StoreManager();
        this.storeManager.initialize();

        CommonLib commonLib = CustomLib.getCustomLib().getCommonLib();

        commonLib.getApiListeners().add(new CoreListener());

        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        pigeon.getConvertersRegistry()
                .registerConvertersInPackage(Config.CONVERTERS_PACKAGE.format());

        pigeon.getPayloadsRegistry()
                .registerPayloadsInPackage(Config.PAYLOAD_PACKAGE.format());

        pigeon.getListenersRegistry()
                .registerListener(new Listeners());

        this.elevatedStaff = Arrays.asList(
                UUID.fromString("239c8573-0675-4a78-82f3-d178c72d5dc9"), // oHate
                UUID.fromString("91121d88-8a07-4bef-bc78-c80484e1b6b6"), // ByteCrack
                UUID.fromString("a210da7b-e671-42f3-bf5b-eeec0282b5cb"), // Custom
                UUID.fromString("ccff0c05-64eb-4383-8cd3-ef3215652558")  // FreakingChicken
        );

        registeredCommands = new ArrayList<>();

        DisabledCommands.Initialize();

        for (Class<?> aClass : ClassUtils.getClassesInPackage(Flame.class, Config.COMMAND_PACKAGE.format())) {
            registerCommand(this, aClass);
        }

        Arrays.asList(
//                new AuthListener(),
                new ProfileListener(),
                new ChatListener(),
                new DisabledCommands()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));

        ChatFilter.init();

        // TODO: Punishment Expiration Task for Mutes

        // Tab and Tab Sorting Task
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Flame.getFlame(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Tab.updateTab(player);
            }
        }, 0L, 20L);

        // Grant Expiration Task
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ProfileUtil.getAssociatedProfile(player)
                            .thenAccept(profile -> {
                                for (Grant grant : profile.getCachedGrants()) {
                                    if (grant.api().hasExpired() && !grant.isRemoved()) {
                                        profile.api().expireGrant(grant, System.currentTimeMillis());
                                    }
                                }
                            });
                });
            }
        }.runTaskTimerAsynchronously(Flame.getFlame(), 0, 20);

        // Server Update Task
        new BukkitRunnable() {
            @Override
            public void run() {
                CustomLib.getCustomLib().getPigeon().broadcast(new ServerUpdatePayload(
                        Config.SERVER_NAME.format(),
                        Gamemode.getByName(Config.SERVER_TYPE.format()),
                        ManagementFactory.getRuntimeMXBean().getUptime(),
                        Bukkit.getServer().getTPS()[0],
                        Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()),
                        Bukkit.getMaxPlayers()
                ));
            }
        }.runTaskTimerAsynchronously(this, 0, 20 * 5);

        FlameAPI.setStartingUp(false);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().shutdown();
    }

}
