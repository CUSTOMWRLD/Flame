package com.customwrld.flame;

import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.customlib.util.ClassUtils;
import com.customwrld.flame.chat.ChatFilter;
import com.customwrld.flame.chat.ChatListener;
import com.customwrld.flame.profile.AuthListener;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.ProfileListener;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.rank.Rank;
import com.customwrld.flame.store.StoreManager;
import com.customwrld.flame.util.Command;
import com.customwrld.flame.util.DisabledCommands;
import com.customwrld.flame.util.Placeholders;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public final class Flame extends JavaPlugin {

    // TODO: For each pigeon broadcast make sure to run the code on the current server also if the player is on

    @Getter
    private static Flame flame;

    private ConfigurationFile mainConfig;

    private GoogleAuthenticator auth;

    private StoreManager storeManager;

    private Placeholders placeholders;

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

        this.mainConfig = new ConfigurationFile(this, "config");

        this.auth = new GoogleAuthenticator();

        this.storeManager = new StoreManager();
        this.storeManager.initialize();

        this.placeholders = new Placeholders("CUSTOMWRLD", "flame", flame.getDescription().getVersion());
        this.placeholders.register();

        this.elevatedStaff = Arrays.asList(
                UUID.fromString("239c8573-0675-4a78-82f3-d178c72d5dc9"), // oHate
                UUID.fromString("91121d88-8a07-4bef-bc78-c80484e1b6b6"), // ByteCrack
                UUID.fromString("a210da7b-e671-42f3-bf5b-eeec0282b5cb"), // Custom
                UUID.fromString("ccff0c05-64eb-4383-8cd3-ef3215652558")  // FreakingChicken
        );

        registeredCommands = new ArrayList<>();

        DisabledCommands.Initialize();

        for (Class<?> aClass : ClassUtils.getClassesInPackage(Flame.class, this.mainConfig.getString("REFLECTION.COMMAND_PACKAGE"))) {
            registerCommand(this, aClass);
        }

        Rank.init();

        Arrays.asList(
                new AuthListener(),
                new ProfileListener(),
                new ChatListener(),
                new DisabledCommands()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));

        ChatFilter.init();

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
                            Grant.expireGrant(profile, grant);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Flame.getFlame(), 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (FlameAPI.isShuttingDown()) {
                    this.cancel();
                    return;
                }

//                CustomLib.getCustomLib().getPigeon().broadcast(new ServerUpdatePayload(getFlameServer()));
            }
        }.runTaskTimerAsynchronously(flame, 0, 20 * 5);

        FlameAPI.setStartingUp(false);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().shutdown();
    }

//    public Server getFlameServer() {
//        return new Server(
//                Config.SERVER_NAME.format(),
//                Server.Type.valueOf(Config.SERVER_TYPE.format()),
//                ManagementFactory.getRuntimeMXBean().getUptime(),
//                Bukkit.getServer().getTPS()[0],
//                Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()),
//                Bukkit.getMaxPlayers()
//        );
//    }

}
