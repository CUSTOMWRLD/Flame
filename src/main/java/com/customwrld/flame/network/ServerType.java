package com.customwrld.flame.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum ServerType {
    LOBBY("Lobbies", "lobby", Material.CLOCK),
    SURVIVAL("Survival", "survival", Material.GRASS_BLOCK),
    SKYBLOCK("SkyBlock", "skyblock", Material.WATER_BUCKET),
    PRACTICE("Practice", "practice", Material.DIAMOND_SWORD),
    SKYWARS("SkyWars", "sw", Material.ENDER_EYE),
    WOOLWARS("WoolWars", "ww", Material.WHITE_WOOL),
    BEDWARS("BedWars", "bw", Material.RED_BED);

    private final String display;
    private final String prefix;
    private final Material icon;
}
