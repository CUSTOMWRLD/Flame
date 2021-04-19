package com.customwrld.flame.store;

import com.customwrld.flame.Flame;
import lombok.Getter;

public class StoreManager {
    @Getter
    private String domain;

    public void initialize() {
        domain = Flame.get().getMainConfig().getString("TEBEX.URL");
    }
}
