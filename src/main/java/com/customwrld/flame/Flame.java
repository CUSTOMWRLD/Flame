package com.customwrld.flame;

import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.flame.pigeon.Pigeon;
import com.customwrld.flame.store.StoreManager;
import com.customwrld.flame.util.Initialize;
import com.customwrld.flame.util.Placeholders;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Flame extends JavaPlugin {

    private static Flame instance;

    @Getter @Setter private Placeholders placeholders;
    @Getter private ConfigurationFile mainConfig;
    @Getter private Pigeon pigeon;
    @Getter private GoogleAuthenticator auth;
    @Getter private StoreManager storeManager;
    @Getter private Set<UUID> elevatedStaff;
    @Getter @Setter private boolean chatMuted;
    @Getter @Setter private boolean shuttingDown;

    public static Flame get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        mainConfig = new ConfigurationFile(this, "config");
        elevatedStaff = new HashSet<>();

        pigeon = new Pigeon("flame");
        pigeon.setupAQMP();

        auth = new GoogleAuthenticator();

        storeManager = new StoreManager();

        chatMuted = false;
        shuttingDown = false;

        Initialize.init(this);
    }

    @Override
    public void onDisable() {
        pigeon.disconnect();
        Bukkit.getServer().shutdown();
    }
}
