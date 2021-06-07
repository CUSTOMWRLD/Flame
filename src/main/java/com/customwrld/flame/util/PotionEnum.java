package com.customwrld.flame.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum PotionEnum {
    DRINKABLE("drinkable", "Potion of", Material.POTION),
    SPLASHABLE("splashable", "Splash Potion of", Material.SPLASH_POTION),
    LINGERING("lingering", "Lingering Potion of", Material.LINGERING_POTION);

    private final String display;
    private final String name;
    private final Material material;
}
