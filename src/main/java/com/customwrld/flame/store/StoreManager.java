package com.customwrld.flame.store;

import com.customwrld.customlib.configuration.type.ConfigurationFile;
import com.customwrld.flame.Flame;
import com.customwrld.flame.store.tebex.TebexAPI;
import lombok.Getter;

public class StoreManager {

    @Getter
    private String domain;

    public void initialize() {
        ConfigurationFile config = Flame.getFlame().getMainConfig();

        TebexAPI.setSecret(config.getString("TEBEX.SECRET"));
        this.domain = config.getString("TEBEX.URL");
    }
}
