package com.customwrld.flame.util;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.commonlib.modules.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {
    private final String author;
    private final String identifier;
    private final String version;

    public Placeholders(String author, String identifier, String version) {
        this.author = author;
        this.identifier = identifier;
        this.version = version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getVersion() {
        return version;
    }

    // TODO: Placeholders
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
//        Profile profile = Profile.getProfile(null, player.getUniqueId());
//        Rank rank = profile.api().getActiveGrant().api().getRank();
        return switch (identifier) {
//            case ("format") -> FlameAPI.getChatFormat(profile);
//            case ("name") -> FlameAPI.getColoredName(profile);
//            case ("prefix") -> FlameAPI.getColoredPrefix(profile);
//            case ("rank") -> ChatColor.of(rank.getColor()) + rank.getName();
//            case ("server") -> Config.SERVER_NAME.format();
//            case ("weight") -> String.valueOf(rank.getWeight());
            default -> null;
        };
    }
}