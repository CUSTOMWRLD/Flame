package com.customwrld.flame.profile.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true, new PunishmentTypeData("Blacklists", ChatColor.DARK_RED, Material.BLACK_WOOL)),
    BAN("Ban", "banned", "unbanned", true, true, new PunishmentTypeData("Bans", ChatColor.RED, Material.RED_WOOL)),
    MUTE("Mute", "muted", "unmuted", false, true, new PunishmentTypeData("Mutes", ChatColor.GOLD, Material.ORANGE_WOOL)),
    WARN("Warning", "warned", null, false, false, new PunishmentTypeData("Warnings", ChatColor.GREEN, Material.LIME_WOOL)),
    KICK("Kick", "kicked", null, false, false, new PunishmentTypeData("Kicks", ChatColor.BLUE, Material.LIGHT_BLUE_WOOL));

    private final String readable;
    private final String context;
    private final String undoContext;
    private final boolean ban;
    private final boolean removable;
    private final PunishmentTypeData typeData;

    @AllArgsConstructor
    @Getter
    public static class PunishmentTypeData {
        private final String readable;
        private final ChatColor color;
        private final Material material;
    }

}
